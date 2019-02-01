[getLookupApplicationList]
select a.* 
from (
	select objid 
	from loan_application
	where borrower_name like $P{searchtext}
	union 
	select objid
	from loan_application 
	where appno like $P{searchtext}
) q
inner join loan_application a on q.objid=a.objid
order by a.borrower_name, a.dtreleased desc
