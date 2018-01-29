[getList]
SELECT a.*
FROM ledgeramnesty_smc a
WHERE a.borrower_name LIKE $P{searchtext}
	AND a.txnmode = $P{mode}
ORDER BY a.dtcreated DESC

[getListByState]
SELECT a.*
FROM ledgeramnesty_smc a
WHERE a.borrower_name LIKE $P{searchtext}
	AND a.txnmode = $P{mode}
	AND a.txnstate = $P{state}
ORDER BY a.dtcreated DESC

[getLookupList]
SELECT a.*
FROM ledgeramnesty_smc a
WHERE a.borrower_name LIKE $P{searchtext}
	AND a.txnmode = $P{mode}
ORDER BY a.dtcreated DESC

[getDocumentsByReftype]
SELECT d.*
FROM ledgeramnesty_smc_document d
WHERE d.parentid = $P{objid}
	AND d.reftype = $P{reftype}

[getFeesByReftype]
SELECT d.*
FROM ledgeramnesty_smc_fee d
WHERE d.parentid = $P{objid}
	AND d.reftype = $P{reftype}

[getPostingHeaders]
SELECT h.*
FROM ledgeramnesty_smc_posting_header h
WHERE h.refid = $P{objid}
ORDER BY h.sequence

[getPostingDetails]
SELECT d.*
FROM ledgeramnesty_smc_posting_detail d
WHERE d.parentid = $P{objid}
ORDER BY d.sequenceno, d.idx

[getConditions]
SELECT c.*
FROM ledgeramnesty_smc_condition c
WHERE c.parentid = $P{objid}

[removePostingDetail]
DELETE FROM ledgeramnesty_smc_posting_detail
WHERE parentid = $P{objid}

[removePostingHeader]
DELETE FROM ledgeramnesty_smc_posting_header
WHERE refid = $P{objid}

[removeCondition]
DELETE FROM ledgeramnesty_smc_condition
WHERE parentid = $P{objid}

[findLastDetailItem]
SELECT d.*
FROM ledgeramnesty_smc_posting_detail d
WHERE d.parentid = $P{objid}
ORDER BY d.sequenceno DESC, d.idx DESC

[findLastDetailItemNotAdjustment]
select d.*
from ledgeramnesty_smc_posting_detail d
left join loan_ledger_adjustment a on d.objid = a.objid
where d.parentid =  $P{objid}
	and a.objid is null
order by d.sequenceno desc, d.idx desc

[findCountPostingDetails]
SELECT COUNT(d.objid) AS counter
FROM ledgeramnesty_smc_posting_detail d
WHERE d.parentid = $P{objid}
GROUP BY d.parentid
HAVING counter >= 0

[findCountPostingDetailsByPaymentid]
SELECT COUNT(d.objid) AS counter
FROM ledgeramnesty_smc_posting_detail d
WHERE d.parentid = $P{objid}
	AND d.paymentid = $P{paymentid}
GROUP BY d.parentid
HAVING counter >= 0

[changeState]
UPDATE ledgeramnesty_smc SET txnstate = $P{txnstate}
WHERE objid = $P{objid}