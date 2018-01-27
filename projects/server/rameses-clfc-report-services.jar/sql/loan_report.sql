[getLoansByRoute]
SELECT q.*,
	CASE WHEN lb.objid IS NOT NULL THEN
		CONCAT(q.bname, ' AND ', lb.borrowername)
	ELSE
		q.bname
	END AS borrower_name
FROM (
	SELECT a.appno AS loanapp_appno, a.apptype AS loanapp_apptype, a.loanamount AS loanapp_loanamount, a.loantype AS loanapp_loantype,
		a.borrower_objid AS borrower_objid, a.borrower_name AS bname, a.objid AS loanapp_objid, ac.dtreleased AS loanapp_dtreleased, 
		l.dtmatured AS loanapp_dtmatured, l.objid AS loanapp_ledgerid, r.code AS route_code, r.description AS route_description, 
		r.area AS route_area, l.balance AS loanapp_balance, b.address AS loanapp_address,
		(SELECT objid FROM loanapp_borrower WHERE parentid = l.appid AND `type` = 'JOINT' LIMIT 1) AS jointid
	FROM loanapp a
	INNER JOIN loanapp_capture ac ON a.objid = ac.objid
	INNER JOIN loan_ledger l ON a.objid = l.appid
	INNER JOIN loan_route r ON a.route_code = r.code
	INNER JOIN loan_ledger_segregation s ON l.objid = s.refid
	INNER JOIN borrower b ON a.borrower_objid = b.objid
	WHERE l.state = 'OPEN'
		AND r.code = $P{routecode}
	ORDER BY b.address, a.borrower_name, ac.dtreleased DESC
) q LEFT JOIN loanapp_borrower lb ON q.jointid = lb.objid

[xgetLoansByRoute]
SELECT a.appno AS loanapp_appno, a.apptype AS loanapp_apptype, a.loanamount AS loanapp_loanamount, a.loantype AS loanapp_loantype,
	a.borrower_objid AS borrower_objid, a.borrower_name AS borrower_name, a.objid AS loanapp_objid, ac.dtreleased AS loanapp_dtreleased, 
	l.dtmatured AS loanapp_dtmatured, l.objid AS loanapp_ledgerid, r.code AS route_code, r.description AS route_description, 
	r.area AS route_area, l.balance AS loanapp_balance, b.address AS loanapp_address
FROM loanapp a
INNER JOIN loanapp_capture ac ON a.objid = ac.objid
INNER JOIN loan_ledger l ON a.objid = l.appid
INNER JOIN loan_route r ON a.route_code = r.code
INNER JOIN loan_ledger_segregation s ON l.objid = s.refid
INNER JOIN borrower b ON a.borrower_objid = b.objid
WHERE l.state = 'OPEN'
	AND r.code = $P{routecode}
ORDER BY b.address, a.borrower_name, ac.dtreleased DESC

[getLoansByRouteAndSegregationType]
SELECT q.*,
	CASE WHEN lb.objid IS NOT NULL THEN
		CONCAT(q.bname, ' AND ', lb.borrowername)
	ELSE
		q.bname
	END AS borrower_name,
	case when q.alp > 0 then q.alp else 0 end as agingsincelastpayment,
	case when q.amd > 0 then q.amd else 0 end as agingsincematuritydate
FROM (
	SELECT q.*, p.objid AS lastpayment_objid, p.amount AS lastpayment_amount, p.txndate AS lastpayment_txndate, p.refno AS lastpayment_refno,
		datediff(curdate(), p.txndate) as alp, datediff(curdate(), q.loanapp_dtmatured) as amd
	FROM (
		SELECT a.appno AS loanapp_appno, a.apptype AS loanapp_apptype, a.loanamount AS loanapp_loanamount, a.loantype AS loanapp_loantype,
			a.borrower_objid AS borrower_objid, a.borrower_name AS bname, a.objid AS loanapp_objid, ac.dtreleased AS loanapp_dtreleased, 
			l.dtmatured AS loanapp_dtmatured, l.objid AS loanapp_ledgerid, r.code AS route_code, r.description AS route_description, 
			r.area AS route_area, l.balance AS loanapp_balance, b.address AS loanapp_address,
			(SELECT objid FROM loanapp_borrower WHERE parentid = l.appid AND `type` = 'JOINT' LIMIT 1) AS jointid,
			(SELECT objid FROM loan_ledger_payment WHERE parentid = l.objid ORDER BY txndate DESC LIMIT 1) AS lastpaymentid
		FROM loanapp a
		INNER JOIN loanapp_capture ac ON a.objid = ac.objid
		INNER JOIN loan_ledger l ON a.objid = l.appid
		INNER JOIN loan_route r ON a.route_code = r.code
		INNER JOIN loan_ledger_segregation s ON l.objid = s.refid
		INNER JOIN borrower b ON a.borrower_objid = b.objid
		WHERE l.state = 'OPEN'
			AND r.code = $P{routecode}
			AND s.typeid = $P{typeid}
		ORDER BY b.address, a.borrower_name, ac.dtreleased DESC
	) q LEFT JOIN loan_ledger_payment p ON q.lastpaymentid = p.objid
) q LEFT JOIN loanapp_borrower lb ON q.jointid = lb.objid

[xxgetLoansByRouteAndSegregationType]
SELECT q.*,
	CASE WHEN lb.objid IS NOT NULL THEN
		CONCAT(q.bname, ' AND ', lb.borrowername)
	ELSE
		q.bname
	END AS borrower_name
FROM (
	SELECT a.appno AS loanapp_appno, a.apptype AS loanapp_apptype, a.loanamount AS loanapp_loanamount, a.loantype AS loanapp_loantype,
		a.borrower_objid AS borrower_objid, a.borrower_name AS bname, a.objid AS loanapp_objid, ac.dtreleased AS loanapp_dtreleased, 
		l.dtmatured AS loanapp_dtmatured, l.objid AS loanapp_ledgerid, r.code AS route_code, r.description AS route_description, 
		r.area AS route_area, l.balance AS loanapp_balance, b.address AS loanapp_address,
		(SELECT objid FROM loanapp_borrower WHERE parentid = l.appid AND `type` = 'JOINT' LIMIT 1) AS jointid
	FROM loanapp a
	INNER JOIN loanapp_capture ac ON a.objid = ac.objid
	INNER JOIN loan_ledger l ON a.objid = l.appid
	INNER JOIN loan_route r ON a.route_code = r.code
	INNER JOIN loan_ledger_segregation s ON l.objid = s.refid
	INNER JOIN borrower b ON a.borrower_objid = b.objid
	WHERE l.state = 'OPEN'
		AND r.code = $P{routecode}
		AND s.typeid = $P{typeid}
	ORDER BY b.address, a.borrower_name, ac.dtreleased DESC
) q LEFT JOIN loanapp_borrower lb ON q.jointid = lb.objid

[xgetLoansByRouteAndSegregationType]
SELECT a.appno AS loanapp_appno, a.apptype AS loanapp_apptype, a.loanamount AS loanapp_loanamount, a.loantype AS loanapp_loantype,
	a.borrower_objid AS borrower_objid, a.borrower_name AS borrower_name, a.objid AS loanapp_objid, ac.dtreleased AS loanapp_dtreleased, 
	l.dtmatured AS loanapp_dtmatured, l.objid AS loanapp_ledgerid, r.code AS route_code, r.description AS route_description, 
	r.area AS route_area, l.balance AS loanapp_balance, b.address AS loanapp_address
FROM loanapp a
INNER JOIN loanapp_capture ac ON a.objid = ac.objid
INNER JOIN loan_ledger l ON a.objid = l.appid
INNER JOIN loan_route r ON a.route_code = r.code
INNER JOIN loan_ledger_segregation s ON l.objid = s.refid
INNER JOIN borrower b ON a.borrower_objid = b.objid
WHERE l.state = 'OPEN'
	AND r.code = $P{routecode}
	AND s.typeid = $P{typeid}
ORDER BY b.address, a.borrower_name, ac.dtreleased DESC

[getReportDataForAdvanceLoanApplication]
SELECT a.appno, a.borrower_name, l.state, l.overpaymentamount AS overpayment, l.balance, 
	c.dtreleased, l.dtmatured, a.loanamount,
	CASE 
		WHEN l.paymentmethod = 'schedule' THEN 'Schedule/Regular'
		WHEN l.paymentmethod = 'over' THEN 'Overpayment'
	END AS paymentmethod
FROM (
	SELECT q.objid
	FROM (
		SELECT a.objid, a.loanamount * $P{percent} AS threshold, l.balance
		FROM loanapp a 
		INNER JOIN loan_ledger l ON a.objid = l.appid
		WHERE l.state = 'OPEN'
			AND l.paymentmethod = 'over'
			AND CURDATE() <= l.dtmatured
		GROUP BY l.objid
		HAVING l.balance <= threshold
	) q
	UNION
	SELECT q.objid
	FROM (
		SELECT l.appid AS objid, DATEDIFF(l.dtmatured, CURDATE()) AS daysdiff
		FROM loan_ledger l
		WHERE l.state = 'OPEN'
			AND l.paymentmethod = 'schedule'
			AND CURDATE() <= l.dtmatured
		GROUP BY l.objid
		HAVING daysdiff <= $P{days} AND daysdiff >= 0
	) q
) q
INNER JOIN loanapp a ON a.objid = q.objid
INNER JOIN loan_ledger l ON a.objid = l.appid
LEFT JOIN loanapp_capture c ON a.objid = c.objid
ORDER BY a.borrower_name
