[getList]
select * from ils_sys_fact

[getCategories]
select * from ils_sys_fact_category where factid=$P{objid}

[getFields]
select * from ils_sys_factfield where parentid=$P{objid} order by seqno

[removeCategories]
delete from ils_sys_fact_category where factid=$P{objid}

[removeFields]
delete from ils_sys_factfield where parentid=$P{objid}