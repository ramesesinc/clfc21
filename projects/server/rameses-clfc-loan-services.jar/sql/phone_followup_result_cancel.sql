[findCancelRequestByRefidAndState]
SELECT p.*
FROM phone_followup_result_cancel p
WHERE p.refid = $P{refid}
	AND p.txnstate = $P{state}
ORDER BY p.dtcreated DESC
