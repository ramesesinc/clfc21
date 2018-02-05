[getPostedCollectionByDate]
select r.objid as remittanceid, r.collector_name as collector, r.cbsno,
	(SELECT MIN(control_seriesno) FROM loanpayment_detail WHERE parentid = r.objid) AS startseries,
	(SELECT MAX(control_seriesno) FROM loanpayment_detail WHERE parentid = r.objid) AS endseries,
	IFNULL((SELECT ABS(SUM(amount)) FROM collection_remittance_other WHERE parentid = r.objid AND txntype = 'SHORTAGE'), 0) AS shortage,
	IFNULL((SELECT SUM(amount) FROM collection_remittance_other WHERE parentid = r.objid AND txntype = 'OVERAGE'), 0) AS overage,
	IFNULL((select sum(amount) from collection_remittance_detail where parentid = r.objid), 0) as amount,
	IFNULL((select sum(d.amount)
	 from collection_remittance_detail d
	 inner join loanapp a on d.loanapp_objid = a.objid
	 where a.loantype = 'branch'
		and d.parentid = r.objid), 0) as totalbl,
	IFNULL((select sum(d.amount)
	 from collection_remittance_detail d
	 inner join loanapp a on d.loanapp_objid = a.objid
	 where a.loantype <> 'branch'
		and d.parentid = r.objid), 0) as totalnonbl,
	case
		when collection_type <> 'online'
		then
			(select count(d.acctid) 
			 from ledger_billing_item b
			 inner join ledger_billing_detail d on b.objid = d.parentid
			 where b.parentid = r.collection_objid and b.item_objid = r.group_objid)
		when collection_type = 'online' 
		then 
			(select count(d.borrower_objid)
			 from collection_remittance_detail d
			 inner join loanapp a on d.loanapp_objid = a.objid
			 where d.parentid = r.objid
				and a.loantype <> 'branch')
	end as totalaccts,
	IFNULL((select sum(amount) from collection_remittance_detail where parentid = r.objid), 0) as cashremitted,
	(select count(d.loanapp_objid)
	 from collection_remittance_detail d
	 inner join loanapp a on d.loanapp_objid = a.objid
	 where a.loantype <> 'branch' and d.parentid = r.objid) as acctswithpymt,
	CASE 
		WHEN r.group_type = 'route' THEN lr.description
		WHEN r.group_type = 'online' THEN "DIRECT"
		ELSE UCASE(r.group_type)
	END AS route
from collection_remittance r
left join loan_route lr on r.group_objid = lr.code
where r.txndate = $P{date}
	and r.state = "POSTED"

[getCollectionItems]
select c.objid, c.cbsno, (select sum(amount) from collection_cb_detail where parentid = c.objid) as cashremitted, "cbs" as type,
	IFNULL((SELECT ABS(SUM(amount)) FROM collection_remittance_other WHERE parentid = r.objid AND txntype = 'SHORTAGE'), 0) AS shortage,
	IFNULL((SELECT ABS(SUM(amount)) FROM collection_remittance_other WHERE parentid = r.objid AND txntype = 'OVERAGE'), 0) AS overage
from collection_cb c
inner join collection_remittance r on c.collection_objid = r.collection_objid and c.group_objid = r.group_objid
where r.objid = $P{objid}
	and c.state <> 'ENCASHED'
union
select ec.objid, concat(eck.checkno," Encashment") as cbsno, ec.amount as cashremitted, "encashment" as type,
	0 as shortage, 0 as overage
from (
	select c.objid
	from collection_cb c
	inner join collection_remittance r on c.collection_objid = r.collection_objid and c.group_objid = r.group_objid
	where r.objid = $P{objid}
		and c.state = 'ENCASHED'
	order by c.dtfiled 
) q 
	inner join collection_cb c on q.objid = c.objid
	inner join encashment_cbs ec on c.objid = ec.refid
	inner join encashment_check eck on ec.parentid = eck.objid
union 
select d.objid, concat(d.check_no," ", d.borrower_name) as cbsno, d.amount as cashremitted, "branch" as type,
	0 as shortage, 0 as overage
from collection_remittance_detail d
inner join loanapp a on d.loanapp_objid = a.objid
where d.parentid = $P{objid}
	and a.loantype = 'branch'

[getPostedOtherReceiptByDate]
select d.*
from (
	select d.objid
	from otherreceipt o
	inner join otherreceipt_detail d on d.parentid=o.objid
	left join otherreceipt_voidrequest v on d.objid=v.refid
	where v.objid is null
		and o.txndate = $P{date}
		and o.txnstate = 'posted'
	union 
	select distinct d.objid
	from otherreceipt o
	inner join otherreceipt_detail d on d.parentid=o.objid
	inner join otherreceipt_voidrequest v on d.objid=v.refid
	where v.txnstate not in ('for_approval', 'approved')
		and o.txndate = $P{date}
		and o.txnstate = 'posted'
) q inner join otherreceipt_detail d on q.objid=d.objid
order by d.description

[xgetPostedOtherReceiptByDate]
select d.*
from otherreceipt o
inner join otherreceipt_detail d on o.objid = d.parentid
where o.txndate = $P{date}
	and o.txnstate = 'posted'
order by d.description

[getConfirmedBankDepositByDate]
select dd.depositslip_controlno as description, dd.depositslip_amount as amount
from deposit d
inner join deposit_detail dd on d.objid = dd.parentid
inner join depositslip ds on dd.refid = ds.objid
where d.txndate = $P{date}
	and d.txnstate = 'confirmed'
	and d.txntype = 'bank'
	and ds.type = 'cash'
union
select dc.checkno as description, dc.amount
from deposit d
inner join deposit_detail dd on d.objid = dd.parentid
inner join depositslip ds on dd.refid = ds.objid
inner join depositslip_check dc on ds.objid = dc.parentid
where d.txndate = $P{date}
	and d.txnstate = 'confirmed'
	and d.txntype = 'bank'
	and ds.type = 'check'
order by description

[getNoncashCollectionByDate]
select d.*
from collection_remittance r
inner join collection_remittance_detail d on r.objid = d.parentid
where r.txndate = $P{date}
	and r.state = 'posted'
	and d.payoption = 'noncash'

[getBreakdownByDate]
select dds.controlno as description, dds.amount
from dailycollection d
inner join dailycollection_depositslip dds on d.objid = dds.parentid
inner join depositslip ds on dds.refid = ds.objid
where d.txndate = $P{date}
	and ds.type = 'cash'
	and d.state in ('for_verification','verified')
union
select ds.controlno as description, dsc.amount
from dailycollection d
inner join dailycollection_depositslip dds on d.objid = dds.parentid
inner join depositslip ds on dds.refid = ds.objid
inner join depositslip_check dsc on ds.objid = dsc.parentid 
where d.txndate = $P{date}
	and ds.type = 'check'
	and d.state in ('for_verification','verified')
order by description


[findPreviousDailyCollectionByDate]
select * 
from dailycollection 
where txndate < $P{date}
order by txndate desc
