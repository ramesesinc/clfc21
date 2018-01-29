[getList]
SELECT s.*
FROM branch_loan_interest s
WHERE s.yearstarted LIKE $P{searchtext}

[getListByState]
SELECT s.*
FROM branch_loan_interest s
WHERE s.yearstarted LIKE $P{searchtext}
	AND s.txnstate = $P{state}

[findActiveSettingByYear]
SELECT s.*
FROM (
	SELECT s.objid, s.yearstarted AS startyear, IFNULL(s.yearended, YEAR(CURDATE())) AS endyear
	FROM branch_loan_interest s
	WHERE s.txnstate = 'ACTIVE'
	GROUP BY s.objid
	HAVING $P{year} BETWEEN startyear AND endyear
) q INNER JOIN branch_loan_interest s ON q.objid = s.objid

[findActiveOverlapping]
SELECT b.*
FROM (
	SELECT s.objid, s.yearstarted AS startyear, CASE WHEN s.yearended IS NULL THEN YEAR(CURDATE()) ELSE s.yearended END AS endyear
	FROM branch_loan_interest s
	WHERE s.txnstate = 'ACTIVE'
	GROUP BY s.objid
	HAVING $P{yearstarted} BETWEEN startyear AND endyear
	UNION	
	SELECT s.objid, s.yearstarted AS startyear, CASE WHEN s.yearended IS NULL THEN YEAR(CURDATE()) ELSE s.yearended END AS endyear
	FROM branch_loan_interest s
	WHERE s.txnstate = 'ACTIVE'
	GROUP BY s.objid
	HAVING $P{yearended} BETWEEN startyear AND endyear
) a INNER JOIN branch_loan_interest b ON a.objid = b.objid

[changeState]
UPDATE branch_loan_interest SET txnstate = $P{txnstate}
WHERE objid = $P{objid}