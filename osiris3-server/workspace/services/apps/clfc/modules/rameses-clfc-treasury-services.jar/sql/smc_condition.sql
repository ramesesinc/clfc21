[getList]
SELECT c.* 
FROM smc_condition c
WHERE c.title LIKE $P{searchtext}

[getLookupList]
SELECT c.* 
FROM smc_condition c
WHERE c.title LIKE $P{searchtext}