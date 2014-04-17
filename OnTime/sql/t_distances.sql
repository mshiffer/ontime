create database ontime;
use ontime;

drop table distances;
create table distances (o1 mediumint NOT NULL, 
o2 mediumint NOT NULL, 
d1 mediumint NOT NULL, 
d2 mediumint NOT NULL, 
distance int NOT NULL, 
duration int NOT NULL, 
index route (o1, o2, d1, d2),
primary key route (o1, o2, d1, d2));

insert into distances values  (1, 1, 1, 1, 2, 3);
insert into distances values  (2, 1, 1, 1, 4, 5);
insert into distances values  (3, 1, 1, 1, 6, 7);
select * from distances;

-- delete from distances where o1=1 and o2 = 1 and d1 = 1 and d2=1 and distance = 1;