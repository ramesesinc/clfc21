[findByPrevbillingid]
SELECT * FROM ledger_billing_assist
WHERE prevbillingid = $P{objid}

[findByPrevbillingidAndPrevitemid]
SELECT * FROM ledger_billing_assist
WHERE prevbillingid = $P{objid}
	and previtemid = $P{itemid}

