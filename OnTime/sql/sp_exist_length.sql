create procedure sp_exist_length (
po1 mediumint,
po2 mediumint,
pd1 mediumint,
pd2 mediumint)

select exists (select 1 from distances where o1=po1 and o2=po2 and d1=pd1 and d2=pd2);


