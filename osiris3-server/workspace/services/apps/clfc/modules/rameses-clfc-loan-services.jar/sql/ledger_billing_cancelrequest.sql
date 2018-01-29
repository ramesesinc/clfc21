[getList]
select * 
from ledger_billing_cancelrequest 
where author_name like $P{searchtext}
order by dtfiled desc

[getListByState]
select * 
from ledger_billing_cancelrequest 
where author_name like $P{searchtext}
	and txnstate = $P{state}
order by dtfiled desc


[findBillingCancelRequestByState]
select *
from ledger_billing_cancelrequest
where billing_itemid=$P{itemid}
	and txnstate=$P{state}