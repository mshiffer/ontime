import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.json.JSONObject;


public class Algorithm {

	int duration = 0;//seconds.
	int distance = 0;//meters

	Connection connection;
	Statement statement;
	ResultSet resultSet;
	
	Logger logger = Logger.getLogger(Algorithm.class);
	
	private void connectToDB()
	{
		try{

			// this will load the MySQL driver, each DB has its own driver
			Class.forName("com.mysql.jdbc.Driver");

			// setup the connection with the DB.
			connection = DriverManager
					.getConnection("jdbc:mysql://localhost:3306/ontime", "mshiffer", "mls1998");

		}
		catch (Exception e)
		{
			System.err.println(e.toString());
		}
	}

	private void disconnectFromDB()
	{
		try{
			connection.close();
		}
		catch(Exception e)
		{
			System.err.println(e.toString());
		}
	}
	
	private int d2intDB(double val)
	{
		int retVal = (int)(val*10000);
		
		return retVal;
	}

	private boolean estimateCached(double origin1, double origin2, double dest1, double dest2)
	{
		boolean retVal = false;
		
		// convert the doubles to int
		int o1 = d2intDB(origin1);
		int o2 = d2intDB(origin2);
		int d1 = d2intDB(dest1);
		int d2 = d2intDB(dest2);

		//See if it's cached
		try {
			statement = connection.createStatement();
			// resultSet gets the result of the SQL query
			resultSet = statement
					.executeQuery("call sp_exist_length ("+o1+","+o2+","+d1+","+d2+")");

			resultSet.next();
			retVal = resultSet.getBoolean(1);	
			
			logger.info("Executed:  " + "call sp_exist_length ("+o1+","+o2+","+d1+","+d2+")");
			logger.info("Value: " + retVal);
		}
		catch (Exception e)
		{
			System.err.println(e.toString());
		}
		
		return retVal;
	}

	private void loadEstimate(double origin1, double origin2, double dest1, double dest2)
	{
		//convert the doubles to int
		int o1 = d2intDB(origin1);
		int o2 = d2intDB(origin2);
		int d1 = d2intDB(dest1);
		int d2 = d2intDB(dest2);
		
		// load estimate
		try {
		      statement = connection.createStatement();
		      // resultSet gets the result of the SQL query
		      resultSet = statement
		          .executeQuery("call sp_get_distance_duration ("+o1+","+o2+","+d1+","+d2+")");
		      
		      resultSet.next();

		      distance = resultSet.getInt(1);
		      duration = resultSet.getInt(2);
		      
		      logger.info("Executed:  " + "call sp_get_distance_duration ("+o1+","+o2+","+d1+","+d2+")");
		      logger.info("Value: distance-" + distance + " duration-" + duration);
		    }
			catch (Exception e)
			{
				System.err.println(e.toString());
			}		
	}

	private void cacheEstimate(double origin1, double origin2, double dest1, double dest2)
	{
		// Convert the doubles to int
		int o1 = d2intDB(origin1);
		int o2 = d2intDB(origin2);
		int d1 = d2intDB(dest1);
		int d2 = d2intDB(dest2);
	
		// save estimate
		try {
		      statement = connection.createStatement();
		      // resultSet gets the result of the SQL query
		      resultSet = statement
		          .executeQuery("call sp_insert_distance_duration ("
		        		  			+o1+","+o2+","+d1+","+d2+","+distance+","+duration+")");
		      
		      logger.info("Executed:  " + "call sp_insert_distance_duration ("
		        		  			+o1+","+o2+","+d1+","+d2+","+distance+","+duration+")");
		    }
			catch (Exception e)
			{
				System.err.println(e.toString());
			}		
	}

	private double roundCoords(double loc)
	{
		return (Math.round(loc*10000)/(10000.0));
	}

	public int googleEstimate(double origin1, double origin2, double dest1, double dest2)
	{

		// Round to 4 decimal places and do the math.  (Close enough)
		double o1 = roundCoords(origin1);
		double o2 = roundCoords(origin2);

		double d1 = roundCoords(dest1);;
		double d2 = roundCoords(dest2);

		try
		{
			//establish connection
			connectToDB();
			
			// If the estimate is cached
			if (estimateCached(o1, o2, d1, d2))
			{
				loadEstimate(o1, o2, d1, d2);
			}
			else// If not cached
			{
				logger.info("Not cached");
				/*InputStream is = new URL("https://maps.googleapis.com/maps/api/distancematrix/json?origins=42.388584,-71.088563&" +
		    		"destinations=42.364014,-71.079572&sensor=false&mode=driving").openStream();*/

				InputStream is = new URL("https://maps.googleapis.com/maps/api/distancematrix/json?origins="+o1+","+o2+
						"&destinations="+d1+","+d2+"&sensor=false&mode=driving").openStream();

				String jsonString = IOUtils.toString(is);
				JSONObject jo = new JSONObject(jsonString);

				logger.info(jsonString);

				JSONObject o = (JSONObject)(jo.getJSONArray("rows").get(0));
				o = (JSONObject)(o.getJSONArray("elements").get(0));
				duration = o.getJSONObject("duration").getInt("value");
				distance = o.getJSONObject("distance").getInt("value");

				cacheEstimate(o1, o2, d1, d2);
			}
		}
		catch (Exception e)
		{
			System.err.println(e.toString());
		}
		finally 
		{
			disconnectFromDB();
		}

		return duration;
	}

	public int getDuration()
	{
		return duration;
	}

	public int getDistance()
	{
		return distance;
	}

	
	
	/*----------------------------------------------------------------------------------------------------------*/
	public static void main(String[] input)
	{
		BasicConfigurator.configure();
		
		//new Algorithm().googleEstimate(42.388584, -71.088563, 42.364014, -71.079572);//469
		Algorithm a = new Algorithm();
		//From my house to work.
		a.googleEstimate(42.388600, -71.088600, 42.364014, -71.079572);//469
		//new Algorithm().googleEstimate(42.388000, -71.088000, 42.364014, -71.079572);//448

		System.out.println("Final distance " + a.getDistance());
		System.out.println("Final duration " + a.getDuration());
	}



	//TODO Measure persons total time to get there and then cache
	//TODO Ping for persons location every 30 seconds and update ETA
	//TODO When ETA < TTCook + 30 + TTTransmit send order
	//TODO Use persons pinged time to estimate speed over entire track.

	//TODO Ulimately need to confound with many people coming
	//TODO Also have to deal with the case when a person is closer than TTC

}
