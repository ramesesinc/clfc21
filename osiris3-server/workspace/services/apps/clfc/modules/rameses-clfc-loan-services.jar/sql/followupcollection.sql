[getList]
SELECT s.* 
FROM followupcollection f
INNER JOIN specialcollection s ON f.objid = s.objid
WHERE s.collector_name LIKE $P{searchtext}
ORDER BY s.dtfiled DESC

[getListByCollectoridAndDate]
select s.*
from specialcollection s
inner join followupcollection f on s.objid=f.objid
where s.collector_objid=$P{collectorid}
	and s.txndate=$P{date}
order by s.dtfiled desc

[getListByState]
SELECT s.* FROM specialcollection s
INNER JOIN followupcollection f ON s.objid = f.objid
WHERE s.collector_name LIKE $P{searchtext}
	AND s.state = $P{state}
ORDER BY s.dtfiled DESC

[getListByBilling]
SELECT s.* 
FROM specialcollection s
INNER JOIN followupcollection f ON s.objid = f.objid
WHERE s.collector_name LIKE $P{searchtext}
	AND s.billingid = $P{billingid}
ORDER BY s.dtfiled DESC