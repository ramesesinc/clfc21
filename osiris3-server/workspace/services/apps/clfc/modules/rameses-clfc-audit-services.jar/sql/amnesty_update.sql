[getList]
SELECT a.*
FROM amnesty_update a
WHERE a.amnesty_refno LIKE $P{searchtext}
ORDER BY a.dtcreated DESC

[getListByState]
SELECT a.*
FROM amnesty_update a
WHERE a.amnesty_refno LIKE $P{searchtext}
	AND a.txnstate = $P{state}
ORDER BY a.dtcreated DESC

[getActiveAmnesty]
SELECT am.*
FROM (
	SELECT a.objid
	FROM amnesty a
	INNER JOIN amnesty_active ac ON a.objid = ac.objid
	WHERE a.refno LIKE $P{searchtext}
	UNION
	SELECT a.objid
	FROM amnesty a
	INNER JOIN amnesty_active ac ON a.objid = ac.objid
	WHERE a.borrower_name LIKE $P{searchtext}
) a INNER JOIN amnesty am ON a.objid = am.objid
ORDER BY am.borrower_name, am.dtstarted DESC

[getActiveAmnestyByMode
SELECT am.*
FROM (
	SELECT a.objid
	FROM amnesty a
	INNER JOIN amnesty_active ac ON a.objid = ac.objid
	WHERE a.refno LIKE $P{searchtext}
	UNION
	SELECT a.objid
	FROM amnesty a
	INNER JOIN amnesty_active ac ON a.objid = ac.objid
	WHERE a.borrower_name LIKE $P{searchtext}
) a INNER JOIN amnesty am ON a.objid = am.objid
WHERE am.txnmode = $P{mode}
ORDER BY am.borrower_name, am.dtstarted DESC

[getFixList]
SELECT af.*
FROM (
	SELECT af.objid
	FROM ledgeramnesty_active ac
	INNER JOIN ledgeramnesty_fix af ON ac.refid = af.objid
	WHERE af.loanapp_appno LIKE $P{searchtext}
	UNION
	SELECT af.objid
	FROM ledgeramnesty_active ac
	INNER JOIN ledgeramnesty_fix af ON ac.refid = af.objid
	WHERE af.borrower_name LIKE $P{searchtext}
	UNION
	SELECT af.objid
	FROM ledgeramnesty_active ac
	INNER JOIN ledgeramnesty_fix af ON ac.refid = af.objid
	WHERE af.refno LIKE $P{searchtext}
) q INNER JOIN ledgeramnesty_fix af ON q.objid = af.objid
ORDER BY af.borrower_name, af.dtstarted DESC


[getSMCList]
SELECT af.*
FROM (
	SELECT af.objid
	FROM ledgeramnesty_active ac
	INNER JOIN ledgeramnesty_smc af ON ac.refid = af.objid
	WHERE af.loanapp_appno LIKE $P{searchtext}
	UNION
	SELECT af.objid
	FROM ledgeramnesty_active ac
	INNER JOIN ledgeramnesty_smc af ON ac.refid = af.objid
	WHERE af.borrower_name LIKE $P{searchtext}
	UNION
	SELECT af.objid
	FROM ledgeramnesty_active ac
	INNER JOIN ledgeramnesty_smc af ON ac.refid = af.objid
	WHERE af.refno LIKE $P{searchtext}
) q INNER JOIN ledgeramnesty_smc af ON q.objid = af.objid
ORDER BY af.borrower_name, af.dtstarted DESC