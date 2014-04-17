package testing;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import com.mysql.jdbc.Driver;

// We need waypoints every 10 seconds driving and walking.
// Preferable to work as our endpoint.
public class SinglePersonSim {

	double startPoint;
	double endPoint;
	
	  private Connection connect = null;
	  private Statement statement = null;
	  private ResultSet resultSet = null;

	
	  public void testDB()
	  {
		   try {
			      // this will load the MySQL driver, each DB has its own driver
			      Class.forName("com.mysql.jdbc.Driver");
			      
			      // setup the connection with the DB.
			      connect = DriverManager
			          .getConnection("jdbc:mysql://localhost:3306/ontime", "mshiffer", "mls1998");
			              //+ "user=mshiffer&password=mls1998");

			      // statements allow to issue SQL queries to the database
			      statement = connect.createStatement();
			      // resultSet gets the result of the SQL query
			      resultSet = statement
			          .executeQuery("call sp_exist_length (1,1,1,1)");
			      
			      resultSet.next();
			      System.out.println(resultSet.getBoolean(1));

			      if (resultSet.getBoolean(1))
			      {
			    	  resultSet = statement.executeQuery("call sp_get_distance_duration (1,1,1,1)");
			    	  
			    	  resultSet.next();
				      System.out.println(resultSet.getInt(1));
				      System.out.println(resultSet.getInt(2));
			    	  
			      }
			      
			      
		   }
		   catch(Exception e)
		   {
			   System.err.println(e.getMessage());
			   System.err.println(e.toString());
		   }
	  }
	  
	public static void main(String[] input)
	{

		SinglePersonSim s = new SinglePersonSim();
		s.testDB();
		
		double a = 4.123456789;

		
		double b = (Math.round(a*10000)/(10000.0));
		
		//b = Math.round(a*10000);
		
		//System.out.println(b);
	}
	
}
