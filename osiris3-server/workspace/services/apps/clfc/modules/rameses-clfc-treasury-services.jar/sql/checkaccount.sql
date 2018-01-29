[getList]
SELECT c.*, b.name AS bank_name
FROM checkaccount c
INNER JOIN bank b ON c.bank_objid = b.objid
WHERE c.checkno LIKE $P{searchtext}
ORDER BY c.txndate DESC, c.dtcreated DESC

[getListByState]
SELECT c.*, b.name AS bank_name
FROM checkaccount c
INNER JOIN bank b ON c.bank_objid = b.objid
WHERE c.checkno LIKE $P{searchtext}
	AND c.state = $P{state}
ORDER BY c.txndate DESC, c.dtcreated DESC

[findByRefid]
SELECT * FROM checkaccount
WHERE refid = $P{refid}

[getLookupForDepositSlip]
SELECT c.*, b.name AS bank_name
FROM checkaccount c
INNER JOIN bank b ON c.bank_objid = b.objid
WHERE c.checkno LIKE $P{searchtext}
	AND c.state = 'cleared'
ORDER BY c.txndate DESC, c.dtcreated DESC

[getLookupListForDepositSlip]
select c.*, b.name as bank_name
from (
	select c.objid
	from checkaccount c
	left join (
		select d.objid, dc.refid
		from depositslip d 
		inner join depositslip_check dc on d.objid = dc.parentid
		where d.state in ('DRAFT', 'FOR_APPROVAL', 'APPROVED', 'FOR_CANCEL', 'CLOSED')
	) q on q.refid = c.objid
	where q.objid is null
) q inner join checkaccount c on q.objid = c.objid
inner join bank b on c.bank_objid = b.objid
where c.checkno like $P{searchtext}
	and c.state = 'cleared'
order by c.txndate DESC, c.dtcreated DESC