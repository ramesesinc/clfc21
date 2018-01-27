[getLedgerLookupList]
SELECT l.objid, a.appno, a.loanamount AS amount, a.borrower_name,
	ac.dtreleased, l.dtmatured, a.loantype
FROM loan_ledger l
INNER JOIN loanapp a ON l.appid = a.objid
LEFT JOIN loanapp_capture ac ON a.objid = ac.objid
WHERE l.state = 'OPEN'
	AND a.borrower_name LIKE $P{searchtext}
ORDER BY a.borrower_name

[getLedgerDetailsByLedgeridAndRefnoWithoutAmnesty]
SELECT d.* 
FROM loan_ledger_detail d
INNER JOIN ledger_detail_state_type s ON d.state = s.name
WHERE d.parentid = $P{ledgerid}
	AND d.refno = $P{refno}
	AND d.amnestyid IS NULL
ORDER BY d.day, d.dtpaid, d.refno, s.level, d.txndate

[getBusinessPayment]
SELECT p.*
FROM loan_ledger_payment p
INNER JOIN loan_ledger_detail d ON p.parentid = d.parentid AND p.txndate = d.dtpaid AND p.refno = d.refno
WHERE p.parentid = $P{objid}
	AND d.state = 'RECEIVED'
	AND d.amnestyid IS NULL
GROUP BY p.objid
HAVING p.objid IS NOT NULL
ORDER BY p.txndate, p.refno

[getSMCPayment]
SELECT p.*
FROM ledgeramnesty_active a
INNER JOIN loan_ledger_payment p ON p.parentid = a.ledgerid
WHERE a.refid = $P{objid}
	AND p.txndate BETWEEN a.dtstarted AND IFNULL(a.dtended, CURDATE())
ORDER BY p.txndate, p.refno

[getBranchPayment]
SELECT p.*
FROM loan_ledger_payment p
WHERE p.parentid = $P{objid}
ORDER BY p.txndate, p.refno

[getFixPayment]
SELECT p.*
FROM ledgeramnesty_active a
INNER JOIN loan_ledger_payment p ON a.ledgerid = p.parentid
WHERE a.refid = $P{objid}
	AND p.txndate BETWEEN a.dtstarted AND IFNULL(a.dtended, CURDATE())
ORDER BY p.txndate, p.refno

[getWaiverPayment]
SELECT p.*
FROM ledgeramnesty_active a
INNER JOIN loan_ledger_payment p ON a.ledgerid = p.parentid
WHERE a.refid = $P{objid}
	AND p.txndate BETWEEN a.dtstarted AND IFNULL(a.dtended, CURDATE())
ORDER BY p.txndate, p.refno

[getFixPostingDetail]
SELECT d.* 
FROM ledgeramnesty_postingdetail d
INNER JOIN ledger_detail_state_type s ON d.state = s.name 
WHERE d.parentid = $P{objid}
	AND d.state <> 'AMNESTY'
ORDER BY d.day, d.dtpaid, d.refno, s.level, d.txndate

[getFixPostingDetailByRefno]
SELECT d.* 
FROM ledgeramnesty_postingdetail d
INNER JOIN ledger_detail_state_type s ON d.state = s.name 
WHERE d.parentid = $P{objid}
	AND d.refno = $P{refno}
	AND d.state <> 'AMNESTY'
ORDER BY d.day, d.dtpaid, d.refno, s.level, d.txndate

[getWaiverPostingDetail]
SELECT d.* 
FROM ledgeramnesty_postingdetail d
INNER JOIN ledger_detail_state_type s ON d.state = s.name 
WHERE d.parentid = $P{objid}
	AND d.state <> 'AMNESTY'
ORDER BY d.day, d.dtpaid, d.refno, s.level, d.txndate

[getWaiverPostingDetailByRefno]
SELECT d.* 
FROM ledgeramnesty_postingdetail d
INNER JOIN ledger_detail_state_type s ON d.state = s.name 
WHERE d.parentid = $P{objid}
	AND d.refno = $P{refno}
	AND d.state <> 'AMNESTY'
ORDER BY d.day, d.dtpaid, d.refno, s.level, d.txndate

[findAmnestyDetail]
SELECT l.*
FROM ledgeramnesty_postingdetail l
WHERE l.parentid = $P{objid}
	AND l.state = 'AMNESTY'

[findLedgerAmnestyDetailByAmnestyid]
SELECT l.*
FROM loan_ledger_detail l
WHERE l.parentid = $P{objid}
	AND l.amnestyid = $P{amnestyid}
	AND l.state = 'AMNESTY'