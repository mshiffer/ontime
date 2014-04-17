create procedure sp_get_distance_duration (
po1 mediumint,
po2 mediumint,
pd1 mediumint,
pd2 mediumint)

select d.distance, d.duration from distances d where o1=po1 and o2=po2 and d1=pd1 and d2=pd2;
