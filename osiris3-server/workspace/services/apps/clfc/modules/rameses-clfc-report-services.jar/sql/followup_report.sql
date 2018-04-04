[getList]
SELECT
	objid,
	txndate,
	dtcreated,
	dtmodified,
	modifiedby_objid,
	modifiedby_name,
	author_objid,
	author_name,
	reportnote
FROM followup_report

[getMergeFollowUpReportDetail]
SELECT 
	objid, 
	refid, 
	ledgerid, 
	borrower_name, 
	txndate, 
	GROUP_CONCAT(remarks SEPARATOR '\r' ) AS remarks, 
	confirmedby_name
FROM followup_result 
WHERE txndate = $P{date} 
GROUP BY ledgerid, refid 
ORDER BY borrower_name