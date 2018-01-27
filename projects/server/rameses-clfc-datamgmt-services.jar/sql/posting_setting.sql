[findCurrentPostingSettingByType]
SELECT s.* 
FROM posting_setting s
WHERE s.type = $P{type}