[getForMigrateList]
select t.* 
from mobile_tracker t
left join mobile_tracker_migrated m on t.objid=m.objid
where t.dtstart < $P{date}
	and m.objid is null
order by t.dtstart desc

[getTrackerDetail]
select d.*
from mobile_tracker_detail d
left join mobile_tracker_detail_migrated m on d.objid=m.objid
where m.objid is null
	and d.parentid=$P{objid}
	and m.objid is null


[getFieldcollectionItemsByTrackerid]
select *
from fieldcollection_item
where trackerid=$P{trackerid}

[getTrackerRoute]
select *
from mobile_tracker_route 
where parentid=$P{objid}