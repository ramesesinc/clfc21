[getList]
SELECT h.*
FROM (
	SELECT h.code
	FROM posting_header h
	WHERE h.code LIKE $P{searchtext}
	UNION
	SELECT h.code 
	FROM posting_header h
	WHERE h.title LIKE $P{searchtext}
) q INNER JOIN posting_header h ON q.code = h.code
ORDER BY h.dtcreated DESC

[getListWithFilter]
SELECT h.*
FROM (
	SELECT h.code
	FROM posting_header h
	WHERE h.code LIKE $P{searchtext}
	UNION
	SELECT h.code 
	FROM posting_header h
	WHERE h.title LIKE $P{searchtext}
) q INNER JOIN posting_header h ON q.code = h.code
${filter}
ORDER BY h.dtcreated DESC

[getListFilteredByCondition]
SELECT h.*
FROM (
	SELECT h.code
	FROM posting_header h
	WHERE h.type = 'DEFAULT'
	UNION
	SELECT h.code
	FROM posting_header h
	WHERE h.code IN (${conditions})
) q INNER JOIN posting_header h ON q.code = h.code


[getLookupList]
SELECT h.*
FROM (
	SELECT h.code
	FROM posting_header h
	WHERE h.code LIKE $P{searchtext}
	UNION
	SELECT h.code 
	FROM posting_header h
	WHERE h.title LIKE $P{searchtext}
) q INNER JOIN posting_header h ON q.code = h.code
ORDER BY h.sequence

[getDefaultList]
SELECT h.*
FROM posting_header h
WHERE h.type = 'DEFAULT'