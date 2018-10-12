[getList]
select * from ils_sys_fact

[getCategories]
select * from ils_sys_fact_category where factid=$P{objid}

[getFields]
select * from ils_sys_factfield where parentid=$P{objid} order by seqno

[getFactsByCategory]
select f.* 
from ils_sys_fact_category c
inner join ils_sys_fact f on c.factid=f.objid
where c.category=$P{category}
group by f.objid

[removeCategories]
delete from ils_sys_fact_category where factid=$P{objid}

[removeFields]
delete from ils_sys_factfield where parentid=$P{objid}