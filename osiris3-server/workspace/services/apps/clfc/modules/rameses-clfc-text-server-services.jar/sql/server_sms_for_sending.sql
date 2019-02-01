[getList]
SELECT s.* 
FROM server_sms_for_sending s
ORDER BY s.dtfiled

[findByRefidAndPart]
SELECT s.*
FROM server_sms_for_sending s
WHERE s.refid = $P{refid}
	AND s.part = $P{part}
ORDRE BY s.dtfiled

[findFirstSmsForSending]
SELECT s.*
FROM server_sms_for_sending s
ORDER BY s.dtfiled