[getList]
SELECT * FROM capture_payment
WHERE collector_name LIKE $P{searchtext}
ORDER BY txndate DESC

[getListByState]
SELECT * FROM capture_payment
WHERE collector_name LIKE $P{searchtext}
	AND state = $P{state}
ORDER BY txndate DESC

[getPendingList]
SELECT c.*
FROM capture_payment_pending cp
INNER JOIN capture_payment c ON cp.objid = c.objid
WHERE c.collector_name LIKE $P{searchtext}
ORDER BY c.txndate DESC

[getDetails]
SELECT * FROM capture_payment_detail
WHERE parentid = $P{objid}

[getBorrowerLookupList]
SELECT l.objid, l.borrower_objid, l.borrower_name, l.objid AS loanapp_objid,
	l.loanamount AS loanapp_loanamount, l.appno AS loanapp_appno, 
	lr.code AS route_code, lr.area AS route_area,
	lr.description AS route_description
FROM loanapp l
INNER JOIN loan_ledger ll ON l.objid = ll.appid
INNER JOIN loan_route lr ON l.route_code = lr.code
WHERE l.borrower_name LIKE $P{searchtext}
	AND ll.state = 'OPEN'

[getBorrowerLookupListByRemmittanceid]
SELECT l.objid, l.borrower_objid, l.borrower_name, l.objid AS loanapp_objid,
	l.loanamount AS loanapp_loanamount, l.appno AS loanapp_appno, 
	lr.code AS route_code, lr.area AS route_area,
	lr.description AS route_description
FROM collection_remittance r
INNER JOIN loanapp l ON r.group_objid = l.route_code
INNER JOIN loan_ledger ll ON l.objid = ll.appid
INNER JOIN loan_route lr ON l.route_code = lr.code
WHERE r.objid = $P{remittanceid}
	AND l.borrower_name LIKE $P{searchtext}
	AND ll.state = 'OPEN'

[getPendingCapturedPaymentCollectorsByDate]
select distinct collector_objid as objid, collector_name as name
from capture_payment_pending 
where txndate=$P{date}
order by collector_name

[getPendingCapturedPaymentsByCollectorAndDate]
select *
from capture_payment_pending 
where txndate=$P{date}
	and collector_objid=$P{collectorid}
	and borrowername like $P{searchtext}
order by dtpaid 

[getCapturedPaymentByCollectorAndDate]
select * 
from capture_payment 
where collector_objid=$P{collectorid}
	and txndate=$P{date}

[getCapturedPaymentCollectorsByDate]
select distinct collector_objid as objid, collector_name as name
from capture_payment
where txndate=$P{date}
order by collector_name

[getCapturedPaymentCollectionsByDateAndCollector]
select * 
from capture_payment 
where txndate=$P{date}
	and collector_objid=$P{collectorid}
order by dtfiled

[findBySpecialCollectionid]
SELECT c.*
FROM capture_payment c
WHERE c.specialcollectionid = $P{specialcollectionid}

[findPendingByFieldcollection]
select * 
from capture_payment_pending 
where fieldcollectionid=$P{objid}

[xfindPendingByFieldcollection]
SELECT c.objid
FROM capture_payment_pending cp
INNER JOIN capture_payment c ON cp.objid = c.objid
WHERE c.fieldcollectionid = $P{objid}

[findByFieldcollectionidAndState]
select *
from capture_payment 
where fieldcollectionid=$P{objid}
	and state=$P{state}

[findSendBack]
SELECT s.*
FROM capture_payment_sendback s
WHERE s.parentid = $P{objid}
ORDER BY s.dtcreated DESC

[changeState]
UPDATE capture_payment SET state = $P{state}
WHERE objid = $P{objid}