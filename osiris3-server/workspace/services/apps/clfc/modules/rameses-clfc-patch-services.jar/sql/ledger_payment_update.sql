[getLookupList]
SELECT l.objid, l.state, a.borrower_name, a.appno, a.loantype,
	a.loanamount AS amount, c.dtreleased, l.dtmatured
FROM (
	SELECT objid
	FROM loanapp
	WHERE borrower_name LIKE $P{searchtext}
	UNION 
	SELECT objid 
	FROM loanapp
	WHERE appno LIKE $P{searchtext}
) q INNER JOIN loanapp a ON q.objid = a.objid
INNER JOIN loan_ledger l ON a.objid = l.appid
LEFT JOIN loanapp_capture c ON a.objid = c.objid
ORDER BY a.borrower_name, a.appno DESC, c.dtreleased DESC

[getLedgerPayments]
SELECT p.*
FROM loan_ledger_payment p
WHERE p.parentid = $P{objid}
	AND p.refno LIKE $P{searchtext}
ORDER BY p.txndate DESC, p.refno DESC

[findLedgerByID]
SELECT l.*
FROM loan_ledger l
WHERE l.objid = $P{objid}

[findPaymentDetailByAppidAndRefno]
SELECT p.*
FROM loanpayment_detail p
WHERE p.refno = $P{refno}
	AND p.loanapp_objid = $P{appid}

[findWithPartialByPaymentid]
SELECT p.* 
FROM collection_withpartial p 
WHERE p.paymentid = $P{paymentid}

[findNoncashByPaymentid]
SELECT c.*
FROM loan_ledger_noncash c
WHERE c.refid = $P{paymentid}

[findProceedByPaymentid]
SELECT p.*
FROM loan_ledger_proceeds p
WHERE p.refid = $P{paymentid}