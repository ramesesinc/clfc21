[getRemittanceByState]
SELECT r.*
FROM collection_remittance r
LEFT JOIN ledger_billing_resolve l ON r.objid = l.objid AND r.state = l.state
WHERE l.objid IS NULL
	AND r.state = $P{state}
	AND r.group_type <> 'online'
ORDER BY r.txndate

[findByIdAndState]
SELECT r.*
FROM ledger_billing_resolve r
WHERE r.objid = $P{objid}
	AND r.state = $P{state}


[xgetRemittanceByState]
SELECT r.*
FROM collection_remittance r
WHERE r.state = $P{state}
ORDER BY r.txndate DESC
