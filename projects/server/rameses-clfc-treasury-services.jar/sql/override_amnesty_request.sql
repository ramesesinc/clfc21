[getList]
SELECT o.* 
FROM (
	SELECT o.objid
	FROM override_amnesty_request o
	WHERE o.amnesty_refno LIKE $P{searchtext}
	UNION
	SELECT o.objid
	FROM override_amnesty_request o
	WHERE o.loanapp_appno LIKE $P{searchtext}
	UNION
	SELECT o.objid
	FROM override_amnesty_request o
	WHERE o.borrower_name LIKE $P{searchtext}
) q INNER JOIN override_amnesty_request o ON q.objid = o.objid
ORDER BY o.dtcreated DESC

[getListByState]
SELECT o.* 
FROM (
	SELECT o.objid
	FROM override_amnesty_request o
	WHERE o.amnesty_refno LIKE $P{searchtext}
		AND o.txnstate = $P{state}
	UNION
	SELECT o.objid
	FROM override_amnesty_request o
	WHERE o.loanapp_appno LIKE $P{searchtext}
		AND o.txnstate = $P{state}
	UNION
	SELECT o.objid
	FROM override_amnesty_request o
	WHERE o.borrower_name LIKE $P{searchtext}
		AND o.txnstate = $P{state}
) q INNER JOIN override_amnesty_request o ON q.objid = o.objid
ORDER BY o.dtcreated DESC, o.refno DESC

[getListByLedgerid]
SELECT o.* 
FROM override_amnesty_request o
WHERE o.ledger_objid = $P{ledgerid}
ORDER BY o.refno DESC, o.dtcreated DESC

[getListFollowupid]
select o.*
from override_amnesty_request o
where o.refid = $P{followupid}
order by o.refno desc, o.dtcreated desc

[getListByRefid]
SELECT o.*
FROM override_amnesty_request o
WHERE o.refid = $P{refid}
ORDER BY o.refno DESC, o.dtcreated DESC


[getAmnestyLookupList]
SELECT a.*
FROM (
	SELECT a.objid
	FROM ledgeramnesty a
	LEFT JOIN ledgeramnesty_active ac ON a.objid = ac.amnestyid
	WHERE ac.objid IS NULL
		AND a.txnstate = 'RETURNED'
		AND a.borrower_name LIKE $P{searchtext}
	UNION
	SELECT a.objid
	FROM ledgeramnesty a
	LEFT JOIN ledgeramnesty_active ac ON a.objid = ac.amnestyid
	WHERE ac.objid IS NULL
		AND a.txnstate = 'RETURNED'
		AND a.loanapp_appno LIKE $P{searchtext}
	UNION
	SELECT a.objid
	FROM ledgeramnesty a
	LEFT JOIN ledgeramnesty_active ac ON a.objid = ac.amnestyid
	WHERE ac.objid IS NULL
		AND a.txnstate = 'RETURNED'
		AND a.refno LIKE $P{searchtext}
) q INNER JOIN ledgeramnesty a ON q.objid = a.objid
ORDER BY a.txndate, a.dtcreated

[getAmnestyLookupListByLedgerid]
SELECT a.*
FROM (
	SELECT a.objid
	FROM ledgeramnesty a
	LEFT JOIN ledgeramnesty_active ac ON a.objid = ac.amnestyid
	WHERE ac.objid IS NULL
		AND a.txnstate = 'RETURNED'
		AND a.borrower_name LIKE $P{searchtext}
		AND a.ledger_objid = $P{ledgerid}
	UNION
	SELECT a.objid
	FROM ledgeramnesty a
	LEFT JOIN ledgeramnesty_active ac ON a.objid = ac.amnestyid
	WHERE ac.objid IS NULL
		AND a.txnstate = 'RETURNED'
		AND a.loanapp_appno LIKE $P{searchtext}
		AND a.ledger_objid = $P{ledgerid}
	UNION
	SELECT a.objid
	FROM ledgeramnesty a
	LEFT JOIN ledgeramnesty_active ac ON a.objid = ac.amnestyid
	WHERE ac.objid IS NULL
		AND a.txnstate = 'RETURNED'
		AND a.refno LIKE $P{searchtext}
		AND a.ledger_objid = $P{ledgerid}
) q INNER JOIN ledgeramnesty a ON q.objid = a.objid
ORDER BY a.txndate, a.dtcreated

[findByState]
SELECT o.*
FROM override_amnesty_request o
WHERE o.txnstate = $P{state}
ORDER BY o.dtcreated DESC

[findByStateAndLedgerid]
SELECT o.*
FROM override_amnesty_request o
WHERE o.txnstate = $P{state}
	AND o.ledger_objid = $P{ledgerid}
ORDER BY o.dtcreated DESC

[findByStateAndRefid]
select o.*
from override_amnesty_request o
where o.txnstate = $P{state}
	and o.refid = $P{refid}
order by o.dtcreated desc
