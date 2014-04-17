create procedure sp_insert_distance_duration (
po1 mediumint,
po2 mediumint,
pd1 mediumint,
pd2 mediumint,
pdistance int,
pduration int)

insert into distances values (po1, po2, pd1, pd2, pdistance, pduration)