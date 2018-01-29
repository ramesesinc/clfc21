[getListByMode]
select a.*
from (
	select objid
	from loan_application
	where txnmode = $P{mode}
		and appno like $P{searchtext}
	union
	select objid
	from loan_application
	where txnmode = $P{mode}
		and borrower_name like $P{searchtext}
) q inner join loan_application a on q.objid = a.objid
order by a.dtcreated desc, a.borrower_name, a.appno desc, a.dtreleased desc

[getListByModeAndState]
select a.*
from (
	select objid
	from loan_application
	where txnmode = $P{mode}
		and txnstate = $P{state}
		and appno like $P{searchtext}
	union
	select objid
	from loan_application
	where txnmode = $P{mode}
		and txnstate = $P{state}
		and borrower_name like $P{searchtext}
) q inner join loan_application a on q.objid = a.objid
order by a.dtcreated desc, a.borrower_name, a.appno desc, a.dtreleased desc

[findProcessedApplicationByBorrowerid]
select *
from loan_application 
where borrower_objid = $P{borrowerid}
	and objid <> $P{objid}
	and txnstate not in('RELEASED','CLOSED')
