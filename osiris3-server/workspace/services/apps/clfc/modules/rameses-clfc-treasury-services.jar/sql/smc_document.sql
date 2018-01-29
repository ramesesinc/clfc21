[getList]
SELECT s.*
FROM (
	SELECT s.code
	FROM smc_document s
	WHERE s.code LIKE $P{searchtext}
	UNION
	SELECT s.code 
	FROM smc_document s
	WHERE s.title LIKE $P{searchtext}
) q INNER JOIN smc_document s ON q.code = s.code
ORDER BY s.dtcreated DESC, s.code

[getLookupList]
SELECT s.*
FROM (
	SELECT s.code
	FROM smc_document s
	WHERE s.code LIKE $P{searchtext}
	UNION
	SELECT s.code 
	FROM smc_document s
	WHERE s.title LIKE $P{searchtext}
) q INNER JOIN smc_document s ON q.code = s.code
ORDER BY s.dtcreated DESC, s.code