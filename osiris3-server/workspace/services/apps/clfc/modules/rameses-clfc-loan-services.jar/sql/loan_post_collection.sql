[getCollectionByDate]
select 
	case when r.objid is null then o.objid else r.objid end as objid, 
	o.dtfiled, o.collector_objid, o.collector_name, 
	case when r.objid is null then o.state else r.state end as state,
	case when r.objid is null 
		then ifnull((select sum(amount) from onlinecollection_detail where parentid=o.objid), 0)
		else ifnull((select sum(amount) from collection_remittance_detail where parentid=r.objid), 0)
	end as totalcollection,
	o.objid as groupid, o.objid as itemid, 'ONLINE' as type, 'online' as grouptype
from onlinecollection o
left join collection_remittance r on o.remittanceid=r.objid
where o.txndate = $P{date}
union
select r.objid, r.dtfiled, r.collector_objid, r.collector_name, r.state, 
	ifnull((select sum(amount) from collection_remittance_detail where parentid=r.objid), 0) as totalcollection,
	r.group_objid as groupid, i.objid as itemid, collection_type as type, r.group_type as grouptype
from collection_remittance r
inner join ledger_billing_item i on r.collection_objid=i.parentid and r.group_objid=i.item_objid
where r.txndate = $P{date}
	and r.group_type <> 'online'
order by dtfiled desc