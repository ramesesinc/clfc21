[getList]
select *
from followup_report
order by dtcreated desc

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
	AND txnstate='CONFIRMED'
GROUP BY ledgerid, refid 
ORDER BY borrower_name