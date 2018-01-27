[getList]
SELECT e.* 
FROM ( 
	SELECT objid FROM accounttype e WHERE e.code LIKE $P{searchtext} 
	UNION 
	SELECT objid FROM accounttype e WHERE e.name LIKE $P{searchtext} 
)bt 
	INNER JOIN accounttype e ON bt.objid=e.objid 
ORDER BY e.name 

[getListByState]
SELECT e.* 
FROM ( 
	SELECT objid FROM accounttype e WHERE e.code LIKE $P{searchtext} 
	UNION 
	SELECT objid FROM accounttype e WHERE e.name LIKE $P{searchtext} 
)bt 
	INNER JOIN accounttype e ON bt.objid=e.objid 
WHERE e.txnstate = $P{state}
ORDER BY e.name 

[getLookupList]
SELECT e.* 
FROM ( 
	SELECT objid FROM accounttype e WHERE e.code LIKE $P{searchtext} 
	UNION 
	SELECT objid FROM accounttype e WHERE e.name LIKE $P{searchtext} 
)bt 
	INNER JOIN accounttype e ON bt.objid=e.objid 
ORDER BY e.name 

[getLookupListByState]
SELECT e.* 
FROM ( 
	SELECT objid FROM accounttype e WHERE e.code LIKE $P{searchtext} 
	UNION 
	SELECT objid FROM accounttype e WHERE e.name LIKE $P{searchtext} 
)bt 
	INNER JOIN accounttype e ON bt.objid=e.objid 
WHERE e.txnstate = $P{state}
ORDER BY e.name 