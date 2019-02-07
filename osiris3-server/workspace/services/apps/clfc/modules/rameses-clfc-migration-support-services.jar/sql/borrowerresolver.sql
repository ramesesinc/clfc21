[getList]
SELECT b.*,
	CASE
		WHEN r.objid IS NULL AND e.objid IS NOT NULL THEN 'UNRESOLVED' 
		WHEN r.objid IS NOT NULL THEN 'RESOLVED'
		ELSE 'ALS'
	END AS state 
FROM (
		SELECT b.objid FROM borrower b
		LEFT JOIN borrower_header_merge hm ON b.objid = hm.objid
		WHERE hm.objid IS NULL AND b.borrowername LIKE $P{searchtext}
		UNION
		SELECT b.objid FROM borrower b
		LEFT JOIN borrower_header_merge hm ON b.objid = hm.objid
		WHERE hm.objid IS NULL AND b.objid LIKE $P{searchtext}
		UNION
		SELECT DISTINCT hm.headerid as objid
		FROM borrower b
		INNER JOIN borrower_header_merge hm ON hm.headerid = b.objid
		WHERE b.objid LIKE $P{searchtext}
		UNION
		SELECT DISTINCT hm.headerid as objid
		FROM borrower b
		INNER JOIN borrower_header_merge hm ON hm.headerid = b.objid
		WHERE b.borrowername LIKE $P{searchtext}
	) q
INNER JOIN borrower b ON q.objid = b.objid
LEFT JOIN borrower_resolved r ON b.objid = r.objid
LEFT JOIN borrower_extinfo e ON b.objid = e.objid

[XgetList]
SELECT b.*,
	CASE
		WHEN r.objid IS NULL AND e.objid IS NOT NULL THEN 'UNRESOLVED' 
		WHEN r.objid IS NOT NULL THEN 'RESOLVED'
		ELSE 'ALS'
	END AS state
FROM (
	SELECT b.objid FROM borrower b
	WHERE b.borrowername LIKE $P{searchtext}
	UNION
	SELECT b.objid FROM borrower b
	WHERE b.objid LIKE $P{searchtext}
) a 
	INNER JOIN borrower b ON a.objid = b.objid
	LEFT JOIN borrower_resolved r ON b.objid = r.objid
	LEFT JOIN borrower_extinfo e ON b.objid = e.objid
ORDER BY b.borrowername

[getResolvedList]
SELECT b.*, 'RESOLVED' AS state FROM borrower b 
LEFT JOIN borrower_header_merge hm ON b.objid = hm.objid
INNER JOIN borrower_resolved r ON b.objid = r.objid
WHERE hm.objid IS NULL
	AND b.borrowername LIKE $P{searchtext} 
UNION
SELECT b.*, 'RESOLVED' AS state FROM borrower b 
LEFT JOIN borrower_header_merge hm ON b.objid = hm.objid 
INNER JOIN borrower_resolved r ON r.objid = b.objid
WHERE hm.objid IS NULL AND b.objid LIKE $P{searchtext}
UNION
SELECT b.*, 'RESOLVED' AS state FROM (
	SELECT DISTINCT hm.headerid 
	FROM borrower b
	INNER JOIN borrower_resolved r ON b.objid = r.objid
	LEFT JOIN borrower_header_merge hm ON b.objid = hm.headerid
	WHERE b.objid LIKE $P{searchtext}
	UNION
	SELECT DISTINCT hm.headerid
	FROM borrower b
	INNER JOIN borrower_resolved r ON b.objid = r.objid
	LEFT JOIN borrower_header_merge hm ON b.objid = hm.objid
	WHERE b.borrowername LIKE $P{searchtext}
) q INNER JOIN borrower b ON b.objid = q.headerid
ORDER BY borrowername

[XgetResolvedList]
SELECT b.*, 'RESOLVED' AS state, e.lastname, e.firstname, e.middlename
FROM (
	SELECT b.objid FROM borrower b
	INNER JOIN borrower_resolved r ON b.objid = r.objid
	WHERE b.borrowername LIKE $P{searchtext}
	UNION
	SELECT b.objid FROM borrower b
	INNER JOIN borrower_resolved r ON b.objid = r.objid
	WHERE b.objid LIKE $P{searchtext}
) a 
	INNER JOIN borrower b ON a.objid = b.objid
	INNER JOIN borrower_extinfo e ON b.objid = e.objid
ORDER BY b.borrowername

[getUnresolvedList]
SELECT b.* , 'UNRESOLVED' AS state FROM borrower b 
INNER JOIN borrower_extinfo e ON b.objid = e.objid
LEFT JOIN borrower_resolved r ON b.objid = r.objid
LEFT JOIN borrower_header_merge hm ON b.objid = hm.objid
WHERE hm.objid IS NULL AND r.objid IS NULL
	AND b.borrowername LIKE $P{searchtext} 
UNION
SELECT b.*, 'UNRESOLVED' AS state FROM borrower b
INNER JOIN borrower_extinfo e ON b.objid = e.objid
LEFT JOIN borrower_resolved r ON b.objid = r.objid
LEFT JOIN borrower_header_merge hm ON b.objid = hm.objid
WHERE hm.objid IS NULL AND r.objid IS NULL AND b.objid LIKE $P{searchtext}
UNION
SELECT b.*, 'UNRESOLVED' AS state FROM (
	SELECT DISTINCT hm.headerid 
	FROM borrower b
	INNER JOIN borrower_extinfo e ON b.objid = e.objid
	INNER JOIN borrower_header_merge hm ON b.objid = hm.headerid
	LEFT JOIN borrower_resolved r ON b.objid = r.objid
	WHERE r.objid IS NULL
		AND b.objid LIKE $P{searchtext}
	UNION
	SELECT DISTINCT hm.headerid
	FROM borrower b
	INNER JOIN borrower_extinfo e ON b.objid = e.objid
	INNER JOIN borrower_header_merge hm ON b.objid = hm.headerid
	LEFT JOIN borrower_resolved r ON b.objid = r.objid
	WHERE r.objid IS NULL
		AND b.borrowername LIKE $P{searchtext}
) q INNER JOIN borrower b ON b.objid = q.headerid
ORDER BY borrowername

[XgetUnresolvedList]
SELECT b.*, 'UNRESOLVED' AS state
FROM (
	SELECT b.objid FROM borrower b
	INNER JOIN borrower_extinfo e ON e.objid = b.objid
	LEFT JOIN borrower_resolved r ON b.objid = r.objid
	WHERE r.objid IS NULL
		AND b.borrowername LIKE $P{searchtext}
	UNION
	SELECT b.objid FROM borrower b
	INNER JOIN borrower_extinfo e ON e.objid = b.objid
	LEFT JOIN borrower_resolved r ON b.objid = r.objid
	WHERE r.objid IS NULL
		AND b.objid LIKE $P{searchtext}
) a 
	INNER JOIN borrower b ON a.objid = b.objid
ORDER BY b.borrowername

[getALSList]
SELECT b.*, 'ALS' AS state FROM borrower b 
LEFT JOIN borrower_header_merge hm ON b.objid = hm.objid
LEFT JOIN borrower_extinfo e ON e.objid = b.objid
WHERE hm.objid IS NULL AND e.objid IS NULL
	AND b.borrowername LIKE $P{searchtext} 
UNION
SELECT b.*, 'ALS' AS state FROM borrower b 
LEFT JOIN borrower_header_merge hm ON b.objid = hm.objid 
LEFT JOIN borrower_extinfo e ON e.objid = b.objid
WHERE hm.headerid = b.objid AND e.objid IS NULL
UNION
SELECT b.*, 'ALS' AS state FROM (
	SELECT DISTINCT hm.headerid
	FROM borrower_header_merge hm
	INNER JOIN borrower b ON hm.objid = b.objid
	WHERE b.objid LIKE $P{searchtext}
	UNION
	SELECT DISTINCT hm.headerid
	FROM borrower_header_merge hm
	INNER JOIN borrower b ON hm.objid = b.objid
	WHERE b.borrowername LIKE $P{searchtext}
) q INNER JOIN borrower b ON b.objid = q.headerid
LEFT JOIN borrower_extinfo e on b.objid = e.objid
WHERE e.objid IS NULL
ORDER BY borrowername


[XgetALSList]
SELECT b.*, 'ALS' AS state
FROM (
	SELECT b.objid FROM borrower b
	LEFT JOIN borrower_extinfo e ON e.objid = b.objid
	WHERE e.objid IS NULL
		AND b.borrowername LIKE $P{searchtext}
	UNION
	SELECT b.objid FROM borrower b
	LEFT JOIN borrower_extinfo e ON e.objid = b.objid
	WHERE e.objid IS NULL
		AND b.objid LIKE $P{searchtext}
) a 
	INNER JOIN borrower b ON a.objid = b.objid
ORDER BY b.borrowername

[getLookupList]
SELECT b.*,
	CASE
		WHEN r.objid IS NULL AND e.objid IS NOT NULL THEN 'UNRESOLVED' 
		WHEN r.objid IS NOT NULL THEN 'RESOLVED'
		ELSE 'ALS'
	END AS state,
	e.lastname, e.firstname, e.middlename
FROM (
	SELECT b.objid FROM borrower b
	WHERE b.borrowername LIKE $P{searchtext}	
	UNION
	SELECT b.objid FROM borrower b
	WHERE b.objid LIKE $P{searchtext}
) a 
	INNER JOIN borrower b ON a.objid = b.objid
	LEFT JOIN borrower_resolved r ON b.objid = r.objid
	LEFT JOIN borrower_extinfo e ON b.objid = e.objid
ORDER BY b.borrowername

[XgetForMigrationList]
SELECT b.*
FROM (
	SELECT DISTINCT br.objid
	FROM loan_resolved lr
	INNER JOIN loan l ON lr.objid = l.objid
	INNER JOIN borrower_resolved br ON l.borrowerid = br.objid
	WHERE lr.taskkey = $P{taskkey}
) a INNER JOIN borrower b ON a.objid = b.objid
ORDER BY b.borrowername

[getForMigrationList]
SELECT b.*, bh.headerid
FROM (
	SELECT DISTINCT br.objid
	FROM loan_resolved lr
	INNER JOIN loan l ON lr.objid=l.objid
	INNER JOIN borrower_resolved br ON l.borrowerid=br.objid
	LEFT JOIN borrower_header_merge bhm ON l.objid=bhm.objid
	WHERE bhm.objid IS NULL
	AND lr.taskkey = $P{taskkey}
	UNION
	SELECT DISTINCT br.objid
	FROM loan_resolved lr
	INNER JOIN loan l ON lr.objid=l.objid
	INNER JOIN borrower_resolved br ON l.borrowerid=br.objid
	INNER JOIN borrower_header_merge bhm ON br.objid=bhm.headerid
	WHERE lr.taskkey = $P{taskkey}
) a INNER JOIN borrower b ON a.objid=b.objid
INNER JOIN borrower_header bh ON b.objid = bh.objid
ORDER BY b.borrowername

[updateResolvedBorrowerKey]
UPDATE borrower_resolved SET taskkey = $P{taskkey}
WHERE taskkey IS NULL
LIMIT 15

[getResolvedBorrowersWithoutKey]
SELECT objid FROM borrower_resolved
WHERE taskkey IS NULL

[getBorrowerWithNoBorrowerHeader]
SELECT b.objid
FROM borrower b
LEFT JOIN borrower_header bh ON b.objid = bh.objid
WHERE bh.objid IS NULL

[validateBorrowerHeader]
SELECT objid FROM borrower_header WHERE objid = $P{objid}
