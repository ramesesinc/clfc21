[getList]
SELECT r.*
FROM loan_ledger_revenue r
WHERE r.parentid = $P{objid}
ORDER BY r.txndate, r.tag

[removeRevenue]
DELETE FROM loan_ledger_revenue
WHERE parentid = $P{objid}