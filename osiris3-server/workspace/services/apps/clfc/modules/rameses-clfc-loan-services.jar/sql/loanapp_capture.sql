[getList]
select a.objid as appid, a.appno, b.objid as borrower_objid, b.name as borrower_name,
	b.address as borrower_address, lc.dtreleased
from (
	select c.objid
	from loanapp_capture_open c
	inner join loanapp a on c.objid=a.objid
	where a.appmode='capture'
		and a.state='released'
		and a.appno like $P{searchtext}
	union
	select c.objid
	from loanapp_capture_open c
	inner join loanapp a on c.objid=a.objid
	where a.appmode='capture'
		and a.state='released'
		and a.borrower_name like $P{searchtext}
) lco
	inner join loanapp_capture lc on lco.objid=lc.objid
	inner join loanapp a on lc.objid=a.objid
	inner join borrower b on a.borrower_objid=b.objid
order by a.appno

[xgetList]
SELECT l.*, 
	b.address AS borrower_address, 
	lr.description AS route_description, lr.area AS route_area, 
	lpt.interestrate AS producttype_interestrate, 
	lpt.pastduerate AS producttype_overduerate, 
	lpt.underpaymentpenalty AS producttype_underpaymentpenalty,
	lpt.absentpenalty AS producttype_absentpenalty,
	lc.dtreleased AS dtreleased
FROM (
	SELECT c.objid
	FROM loanapp_capture_open c
	INNER JOIN loanapp a ON c.objid = a.objid
	WHERE a.appmode = 'CAPTURE'
		AND a.state = 'RELEASED'
		AND a.appno LIKE $P{searchtext}
	UNION
	SELECT c.objid
	FROM loanapp_capture_open c
	INNER JOIN loanapp a ON c.objid = a.objid
	WHERE a.appmode = 'CAPTURE'
		AND a.state = 'RELEASED'
		AND a.borrower_name LIKE $P{searchtext}
) lco
	INNER JOIN loanapp_capture lc ON lco.objid=lc.objid 
	INNER JOIN loanapp l ON lc.objid=l.objid 
	INNER JOIN loan_product_type lpt ON l.producttype_name=lpt.name 	
	INNER JOIN borrower b ON l.borrower_objid=b.objid 
	LEFT JOIN loan_route lr ON l.route_code=lr.code 
ORDER BY l.appno 

[removeOpenApplication]
DELETE FROM loanapp_capture_open WHERE objid=$P{objid} 
