[getList]
SELECT s.*
FROM sms_provider s
WHERE s.code LIKE $P{searchtext}
ORDER BY s.dtcreated DESC

[getPrefixesByProviderCode]
SELECT s.*
FROM sms_provider_prefix s
WHERE s.provider_code = $P{code}

[removePrefixesByProviderCode]
DELETE FROM sms_provider_prefix
WHERE provider_code = $P{code}

[findProviderByPrefix]
SELECT p.*
FROM sms_provider p
INNER JOIN sms_provider_prefix f ON p.code = f.provider_code
WHERE f.prefix = $P{prefix}
