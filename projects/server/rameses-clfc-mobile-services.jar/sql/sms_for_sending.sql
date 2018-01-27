[getList]
SELECT s.* FROM sms_for_sending s
ORDER BY s.dtfiled DESC

[findFirstSmsForSending]
SELECT s.*
FROM sms_for_sending s
ORDER BY s.dtfiled DESC

[findSmsForSendingByRefidAndMobileno]
SELECT s.*
FROM sms_for_sending s
WHERE s.refid = $P{refid}
	AND s.mobileno = $P{mobileno}

[xfindSmsForSendingByPaymentidAndMobileno]
SELECT s.*
FROM sms_for_sending s
WHERE s.paymentid = $P{paymentid}
	AND s.mobileno = $P{mobileno}