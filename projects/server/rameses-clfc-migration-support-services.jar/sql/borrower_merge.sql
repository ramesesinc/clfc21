[getList]
select b.*
from (
	select objid
	from borrower_merge
	where borrower_name like $P{searchtext}
	union 
	select objid
	from borrower_merge
	where author_name like $P{searchtext}
) q inner join borrower_merge b on q.objid=b.objid
order by b.dtcreated desc

[xgetList]
SELECT b.objid, b.state, b.dtcreated, b.author_objid, b.author_name, 
	d.borrower_name
FROM (
	SELECT b.objid
	FROM (
		SELECT b.objid
		FROM borrower_merge b
		WHERE b.author_name LIKE $P{searchtext}
		UNION
		SELECT b.objid
		FROM borrower_merge b
		INNER JOIN borrower_merge_detail d ON b.objid = d.parentid
		WHERE d.borrower_name LIKE $P{searchtext}
	) a INNER JOIN borrower_merge b ON a.objid = b.objid
	WHERE b.objid IS NOT NULL
	GROUP BY b.objid
	HAVING COUNT(b.objid) > 0
) a INNER JOIN borrower_merge b ON a.objid = b.objid
INNER JOIN borrower_merge_detail d ON b.objid = d.parentid
ORDER BY b.dtcreated DESC

[xgetList]
SELECT b.*, d.borrower_name
FROM (
	SELECT b.objid
	FROM borrower_merge b
	WHERE b.author_name LIKE $P{searchtext}
	UNION
	SELECT b.objid
	FROM borrower_merge b
	INNER JOIN borrower_merge_detail d ON b.objid = d.parentid
	WHERE d.borrower_name LIKE $P{searchtext}
) a INNER JOIN borrower_merge b ON a.objid = b.objid
INNER JOIN borrower_merge_detail d ON b.objid = d.parentid
GROUP BY b.objid
HAVING b.objid IS NOT NULL
ORDER BY b.dtcreated DESC

[getListByState]
select b.*
from (
	select objid
	from borrower_merge
	where borrower_name like $P{searchtext}
		and state = $P{state}
	union 
	select objid
	from borrower_merge
	where author_name like $P{searchtext}
		and state = $P{state}
) q inner join borrower_merge b on q.objid=b.objid
order by b.dtcreated desc

[xgetListByState]
SELECT b.objid, b.state, b.dtcreated, b.author_objid, b.author_name, 
	d.borrower_name
FROM (
	SELECT b.objid
	FROM (
		SELECT b.objid
		FROM borrower_merge b
		WHERE b.author_name LIKE $P{searchtext}
			AND b.state = $P{state}
		UNION
		SELECT b.objid
		FROM borrower_merge b
		INNER JOIN borrower_merge_detail d ON b.objid = d.parentid
		WHERE d.borrower_name LIKE $P{searchtext}
			AND b.state = $P{state}
	) a INNER JOIN borrower_merge b ON a.objid = b.objid
	WHERE b.objid IS NOT NULL
	GROUP BY b.objid
	HAVING COUNT(b.objid) > 0
) a INNER JOIN borrower_merge b ON a.objid = b.objid
INNER JOIN borrower_merge_detail d ON b.objid = d.parentid
ORDER BY b.dtcreated DESC

[xgetListByState]
SELECT b.*, d.borrower_name
FROM (
	SELECT b.objid
	FROM borrower_merge b
	WHERE b.author_name LIKE $P{searchtext}
		AND b.state = $P{state}
	UNION
	SELECT b.objid
	FROM borrower_merge b
	INNER JOIN borrower_merge_detail d ON b.objid = d.parentid
	WHERE d.borrower_name LIKE $P{searchtext}
		AND b.state = $P{state}
) a INNER JOIN borrower_merge b ON a.objid = b.objid
INNER JOIN borrower_merge_detail d ON b.objid = d.parentid
GROUP BY b.objid
HAVING b.objid IS NOT NULL
ORDER BY b.dtcreated DESC

[getItems]
SELECT d.*
FROM borrower_merge_detail d
WHERE d.parentid = $P{objid}
ORDER BY d.borrower_objid

[findByBorroweridAndState]
SELECT b.*
FROM borrower_merge_detail d
INNER JOIN borrower_merge b ON d.parentid = b.objid
WHERE d.borrower_objid = $P{borrowerid}
	AND b.state = $P{state}

[changeState]
UPDATE borrower_merge SET state = $P{state}
WHERE objid = $P{objid}