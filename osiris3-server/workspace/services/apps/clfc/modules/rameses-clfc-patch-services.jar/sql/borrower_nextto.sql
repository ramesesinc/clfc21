[getBorrowersByRoutecode]
SELECT b.objid AS borrower_objid, b.name AS borrower_name, q.nextto_objid, 
	q.nextto_name, lb.isstart as borrower_isstart, lb.objid as borrower_recordid,
	q.nextto_recordid
FROM loanapp a
INNER JOIN borrower b ON a.borrower_objid = b.objid
INNER JOIN loanapp_borrower lb on b.objid=lb.borrowerid
LEFT JOIN (
	SELECT l.borrowerid, b.objid AS nextto_objid, b.name AS nextto_name,
		l.objid as nextto_recordid
	FROM borrower b
	INNER JOIN loanapp_borrower_nextto l ON b.objid = l.nexttoid
) q ON b.objid = q.borrowerid
WHERE a.route_code = $P{routecode}
	AND b.name LIKE $P{searchtext}
GROUP BY b.objid
HAVING b.objid IS NOT NULL
ORDER BY b.name

[getAvailableBorrowerLookupList]
SELECT b.*
FROM loanapp a
INNER JOIN borrower b ON a.borrower_objid = b.objid
LEFT JOIN loanapp_borrower_nextto t ON t.nexttoid = b.objid
WHERE a.route_code = $P{routecode}
	AND t.objid IS NULL
	AND b.objid <> $P{borrowerid}
	AND b.name LIKE $P{searchtext}
GROUP BY b.objid
HAVING b.objid IS NOT NULL
ORDER BY b.name

[findNextToByBorrowerid]
SELECT lbn.*
FROM loanapp_borrower_nextto lbn
WHERE lbn.borrowerid = $P{borrowerid}

[findNextToByNexttoid]
SELECT n.*
FROM loanapp_borrower_nextto n
WHERE n.nexttoid = $P{nexttoid}