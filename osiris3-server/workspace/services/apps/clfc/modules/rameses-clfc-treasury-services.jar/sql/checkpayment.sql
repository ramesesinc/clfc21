[getList]
SELECT b.* 
FROM (
	SELECT b.objid FROM checkpayment b 
	WHERE b.refno LIKE $P{searchtext}
	UNION 
	SELECT b.objid FROM checkpayment b 
	WHERE b.checkno LIKE $P{searchtext}
	UNION 
	SELECT b.objid FROM checkpayment b 
	WHERE b.bank_objid LIKE $P{searchtext}
)bt 
	INNER JOIN checkpayment b ON bt.objid=b.objid 
ORDER BY b.dtpaid DESC

[xgetList]
SELECT b.* 
FROM (
	SELECT b.objid FROM checkpayment b 
		INNER JOIN checkpayment_active a ON b.objid=a.objid 
	WHERE b.refno LIKE $P{searchtext}
	UNION 
	SELECT b.objid FROM checkpayment b 
		INNER JOIN checkpayment_active a ON b.objid=a.objid 
	WHERE b.checkno LIKE $P{searchtext}
	UNION 
	SELECT b.objid FROM checkpayment b 
		INNER JOIN checkpayment_active a ON b.objid=a.objid 
	WHERE b.bank_objid LIKE $P{searchtext}
)bt 
	INNER JOIN checkpayment b ON bt.objid=b.objid 
ORDER BY b.dtpaid DESC

[getListByState]
SELECT b.* 
FROM (
	SELECT b.objid FROM checkpayment b 
	WHERE b.refno LIKE $P{searchtext} AND b.state = $P{state}
	UNION 
	SELECT b.objid FROM checkpayment b 
	WHERE b.checkno LIKE $P{searchtext} AND b.state = $P{state} 
	UNION 
	SELECT b.objid FROM checkpayment b 
	WHERE b.bank_objid LIKE $P{searchtext} AND b.state = $P{state} 
)bt 
	INNER JOIN checkpayment b ON bt.objid=b.objid 
ORDER BY b.dtpaid DESC

[getLookupList]
SELECT c.*, b.name AS bank_name
FROM (
	SELECT b.objid FROM checkpayment b 
		INNER JOIN checkpayment_active a ON b.objid=a.objid 
	WHERE b.refno LIKE $P{searchtext}
	UNION 
	SELECT b.objid FROM checkpayment b 
		INNER JOIN checkpayment_active a ON b.objid=a.objid 
	WHERE b.checkno LIKE $P{searchtext}
	UNION 
	SELECT b.objid FROM checkpayment b 
		INNER JOIN checkpayment_active a ON b.objid=a.objid 
	WHERE b.bank_objid LIKE $P{searchtext}
) bt
	INNER JOIN checkpayment c ON bt.objid = c.objid
	INNER JOIN bank b ON b.objid = c.bank_objid
ORDER BY c.dtpaid DESC

[getLookupListByState]
SELECT c.*, b.name AS bank_name
FROM (
	SELECT b.objid FROM checkpayment b 
		INNER JOIN checkpayment_active a ON b.objid=a.objid 
	WHERE b.refno LIKE $P{searchtext} AND b.state=$P{state} 
	UNION 
	SELECT b.objid FROM checkpayment b 
		INNER JOIN checkpayment_active a ON b.objid=a.objid 
	WHERE b.checkno LIKE $P{searchtext} AND b.state=$P{state} 
	UNION 
	SELECT b.objid FROM checkpayment b 
		INNER JOIN checkpayment_active a ON b.objid=a.objid 
	WHERE b.bank_objid LIKE $P{searchtext} AND b.state=$P{state} 
) bt
	INNER JOIN checkpayment c ON bt.objid = c.objid
	INNER JOIN bank b ON b.objid = c.bank_objid
ORDER BY c.dtpaid DESC

[findAppByRefid]
SELECT a.*
FROM (
	SELECT a.objid
	FROM checkpayment c
	INNER JOIN onlinecollection_check oc ON c.refid = oc.objid
	INNER JOIN loanapp a ON oc.loanapp_objid = a.objid
	WHERE c.refid = $P{refid}
	UNION
	SELECT a.objid
	FROM checkpayment c
	INNER JOIN fieldcollection_payment fp ON c.refid = fp.objid
	INNER JOIN fieldcollection_loan fl ON fp.parentid = fl.objid
	INNER JOIN loanapp a ON fl.loanapp_objid = a.objid
	WHERE c.refid = $P{refid}
) q INNER JOIN loanapp a ON q.objid = a.objid


[changeState]
UPDATE checkpayment SET state = $P{state}
WHERE objid = $P{objid}