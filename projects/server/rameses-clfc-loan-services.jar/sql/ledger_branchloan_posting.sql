[getPostingHeaders]
SELECT h.*
FROM ledger_branchloan_posting_header h
WHERE h.refid = $P{objid}
ORDER BY h.sequence

[getPostingDetails]
SELECT d.*
FROM ledger_branchloan_posting_detail d
WHERE d.parentid = $P{objid}
ORDER BY d.sequenceno, d.idx

[findLastDetailItem]
SELECT d.*
FROM ledger_branchloan_posting_detail d
WHERE d.parentid = $P{objid}
ORDER BY d.sequenceno DESC, d.idx DESC

[findLastDetailItemNotAdjustment]
select d.*
from ledger_branchloan_posting_detail d
left join loan_ledger_adjustment a on d.objid = a.objid
where d.parentid = $P{objid}
	and a.objid is null
order by d.sequenceno desc, d.idx desc

[findLastPageIndex]
SELECT COUNT(d.objid) AS ledgercount
FROM ledger_branchloan_posting_detail d
WHERE d.parentid = $P{objid}
ORDER BY d.sequenceno, d.idx

[findLastPageIndexByPaymentid]
SELECT COUNT(d.objid) AS ledgercount
FROM ledger_branchloan_posting_detail d
WHERE d.parentid = $P{objid}
	AND d.paymentid = $P{paymentid}
ORDER BY d.sequenceno, d.idx

[removePostingHeaderByRefid]
DELETE FROM ledger_branchloan_posting_header
WHERE refid = $P{refid}

[removePostingDetails]
DELETE FROM ledger_branchloan_posting_detail
WHERE parentid = $P{objid}