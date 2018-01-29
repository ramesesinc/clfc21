[getList]
SELECT t.*
FROM mobile_tracker t 
where t.branch_objid=$P{branchid}
ORDER BY dtstart ${_ordermode}

[getListByItemid]
SELECT t.*
FROM mobile_tracker t 
inner join mobile_tracker_item i on t.objid=i.parentid
where t.branch_objid=$P{branchid}
	and i.item_objid=$P{itemid}
ORDER BY dtstart ${_ordermode}

[getLogs]
select d.*
from mobile_tracker_detail d
where d.parentid=$P{objid}
order by d.txndate

[getItems]
select *
from mobile_tracker_item
where parentid=$P{objid}

[getRoutesByBranchid]
select r.*
from mobile_tracker_branch_route r
where r.branchid=$P{branchid}
order by r.route_description

[findBeginDetailByPrimary]
SELECT objid FROM mobile_tracker_detail
WHERE parentid = $P{objid}
	AND txndate = $P{date}

[findLastTrackerDetail]
SELECT * FROM mobile_tracker_detail
WHERE parentid =  $P{objid}
ORDER BY txndate DESC

[xgetList]
SELECT t.*, 
	u.username AS user_name, u.lastname AS user_lastname, 
	u.firstname AS user_firstname, u.middlename AS user_middlename 
FROM mobile_tracker t 
	LEFT JOIN sys_user u ON t.userid=u.objid 
ORDER BY dtstart ${_ordermode}

[xgetLogs]
SELECT d.*, m.state AS trackerstate, NULL AS borrower_objid, NULL AS borrower_name
FROM mobile_tracker_detail d 
	INNER JOIN mobile_tracker m ON d.parentid=m.objid 
WHERE d.parentid = $P{parentid} AND d.state=1 
	AND d.reftype = 'TRACK'
UNION
SELECT d.*, m.state AS trackerstate, f.borrower_objid, f.borrower_name
FROM mobile_tracker_detail d 
	INNER JOIN mobile_tracker m ON d.parentid=m.objid 
	INNER JOIN fieldcollection_loan f ON d.refid = f.objid
WHERE d.parentid = $P{parentid} AND d.state=1 
UNION
SELECT d.*, m.state AS trackerstate, p.borrower_objid, p.borrower_name
FROM mobile_tracker_detail d 
	INNER JOIN mobile_tracker m ON d.parentid=m.objid 
	INNER JOIN (
		SELECT fp.objid, fl.borrower_objid, fl.borrower_name
		FROM fieldcollection_payment fp 
		INNER JOIN fieldcollection_loan fl ON fp.parentid = fl.objid
		) p ON d.refid = p.objid
WHERE d.parentid = $P{parentid} AND d.state=1 
ORDER BY txndate 

[xgetDetails]
SELECT d.* 
FROM mobile_tracker_detail d 
WHERE 
	d.parentid=$P{parentid} AND 
	NOT(d.reftype='TRACK') AND 
	d.state=1 
ORDER BY d.txndate 

[xgetDetailsWithBorrower]
SELECT b.*
FROM (
	SELECT d.*, f.borrower_objid, f.borrower_name, 0 AS amount
	FROM mobile_tracker_detail d
	INNER JOIN fieldcollection_loan f ON d.refid = f.objid
	WHERE 
		d.parentid=$P{parentid} AND 
		NOT(d.reftype='TRACK') AND 
		d.state=1 
	UNION
	SELECT d.*, fl.borrower_objid, fl.borrower_name, fp.amount
	FROM mobile_tracker_detail d
	INNER JOIN fieldcollection_payment fp ON d.refid = fp.objid
	INNER JOIN fieldcollection_loan fl ON fp.parentid = fl.objid
	WHERE 
		d.parentid=$P{parentid} AND 
		NOT(d.reftype='TRACK') AND 
		d.state=1 
	) b
ORDER BY b.txndate DESC

[xgetRoutes]
SELECT t.*, 
	r.code AS 'route_code', r.area AS 'route_area', 
	r.description AS 'route_description', r.dayperiod AS 'route_dayperiod' 
FROM mobile_tracker_route t 
	INNER JOIN loan_route r ON t.routecode=r.code 
WHERE 
	parentid=$P{parentid} 

[xfindByPrimary]
SELECT t.*, t.userid AS user_objid, 
	u.username AS user_name, u.lastname AS user_lastname, 
	u.firstname AS user_firstname, u.middlename AS user_middlename  
FROM mobile_tracker t 
	LEFT JOIN sys_user u ON t.userid=u.objid 
WHERE t.objid=$P{objid} 

[xfindLog]
SELECT * FROM mobile_tracker_detail WHERE objid=$P{objid} 


[xfindIsStartedByPrimary]
SELECT objid FROM mobile_tracker
WHERE objid=$P{objid}
	AND dtstart IS NOT NULL

[xfindDetailByParentidAndRefid]
SELECT objid FROM mobile_tracker_detail
WHERE parentid=$P{parentid}
	AND refid=$P{refid}
LIMIT 1

[xfindLastTrackerItemByParentid]
SELECT * FROM mobile_tracker_detail
WHERE parentid =  $P{parentid}
ORDER BY txndate DESC
LIMIT 1

[xfindRouteByParentidAndCode]
SELECT objid FROM mobile_tracker_route
WHERE parentid = $P{parentid}
	AND routecode = $P{routecode}