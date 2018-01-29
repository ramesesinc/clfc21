[findByRefid]
SELECT c.*
FROM collection_withpartial c
WHERE c.refid = $P{refid}

[findByPaymentid]
SELECT c.*
FROM collection_withpartial c
WHERE c.paymentid = $P{paymentid}