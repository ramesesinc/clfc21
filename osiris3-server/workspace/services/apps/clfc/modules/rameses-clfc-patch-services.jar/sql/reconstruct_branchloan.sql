[getDetails]
SELECT d.*
FROM loanapp_branchloan_detail d
WHERE d.parentid = $P{objid}

[getBranchLoanForReconstructList]
SELECT q.*
FROM (
	SELECT a.appno, a.borrower_objid, a.borrower_name, NULL AS branchloanid
	FROM loanapp a
	LEFT JOIN loanapp_branchloan_detail d ON a.objid = d.refid
	WHERE a.loantype = 'BRANCH'
		AND d.objid IS NULL
		AND a.borrower_name LIKE $P{searchtext}
	UNION
	SELECT a.appno, a.borrower_objid, a.borrower_name, NULL AS branchloanid
	FROM loanapp a
	LEFT JOIN loanapp_branchloan_detail d ON a.objid = d.refid
	WHERE a.loantype = 'BRANCH'
		AND d.objid IS NULL
		AND a.appno LIKE $P{searchtext}
	UNION
	SELECT a.appno, a.borrower_objid, a.borrower_name, a.objid AS branchloanid
	FROM loanapp_branchloan a
	WHERE a.borrower_name LIKE $P{searchtext}
	UNION
	SELECT a.appno, a.borrower_objid, a.borrower_name, a.objid AS branchloanid
	FROM loanapp_branchloan a
	WHERE a.appno LIKE $P{searchtext}
) q
GROUP BY q.appno
ORDER BY q.borrower_name

[getRouteLookupList]
SELECT r.*
FROM (
	SELECT r.code
	FROM loan_route r
	WHERE r.code LIKE $P{searchtext}
	UNION 
	SELECT r.code
	FROM loan_route r
	WHERE r.description LIKE $P{searchtext}
) q INNER JOIN loan_route r ON q.code = r.code
ORDER BY r.description

[getProductTypeLookupList]
SELECT p.*
FROM (
	SELECT p.name
	FROM loan_product_type p
	WHERE p.name LIKE $P{searchtext}
	UNION
	SELECT p.name
	FROM loan_product_type p
	WHERE p.description LIKE $P{searchtext}
) q INNER JOIN loan_product_type p ON q.name = p.name
ORDER BY p.name

[getLoanAppByAppnoAndBorrowerid]
SELECT a.*
FROM (
	SELECT a.objid
	FROM loanapp a
	WHERE a.appno = $P{appno}
		AND a.borrower_objid = $P{borrowerid}
	UNION	
	SELECT a.objid
	FROM loanapp a
	WHERE a.appno LIKE $P{searchtext}
		AND a.borrower_objid = $P{borrowerid}
) q INNER JOIN loanapp a ON q.objid = a.objid

[xgetLoanAppByAppnoAndBorrowerid]
SELECT a.*
FROM loanapp a
WHERE a.appno LIKE $P{searchtext}
	AND a.borrower_objid = $P{borrowerid}

[removeDetails]
DELETE FROM loanapp_branchloan_detail
WHERE parentid = $P{objid}