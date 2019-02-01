[getPostedCollectionByDate]
select r.objid as remittanceid, r.collector_name as collector, r.cbsno,
	(SELECT MIN(control_seriesno) FROM loanpayment_detail WHERE parentid = r.objid) AS startseries,
	(SELECT MAX(control_seriesno) FROM loanpayment_detail WHERE parentid = r.objid) AS endseries,
	IFNULL((SELECT ABS(SUM(amount)) FROM collection_remittance_other WHERE parentid = r.objid AND txntype = 'SHORTAGE'), 0) AS shortage,
	IFNULL((SELECT SUM(amount) FROM collection_remittance_other WHERE parentid = r.objid AND txntype = 'OVERAGE'), 0) AS overage,
	IFNULL((select sum(amount) from collection_remittance_detail where parentid = r.objid and payoption not in ('noncash')), 0) as amount,
	IFNULL((select sum(amount) from collection_remittance_detail where parentid = r.objid and payoption not in ('noncash')), 0) as cashremitted,
	IFNULL((select sum(amount) from collection_remittance_detail where parentid = r.objid and payoption = 'noncash'), 0) as noncash,
	CASE 
		WHEN r.group_type = 'route' THEN lr.description
		WHEN r.group_type = 'online' THEN "DIRECT"
		ELSE UCASE(r.group_type)
	END AS route
from collection_remittance r
left join loan_route lr on r.group_objid = lr.code
where r.txndate = $P{date}
	and r.state = "POSTED"
group by remittanceid, collector, cbsno
having amount > 0 or noncash > 0
order by route

[getCollectionItems]
select c.objid, c.cbsno, "" as description, (select sum(amount) from collection_cb_detail where parentid = c.objid) as cashremitted, "cbs" as type
from collection_cb c
inner join collection_remittance r on c.collection_objid = r.collection_objid and c.group_objid = r.group_objid
where r.objid = $P{objid}
	and c.state <> 'ENCASHED'
union
select ec.objid, ds.controlno as cbsno, "Encashment" as description, ec.amount as cashremitted, "encashment" as type
from (
	select c.objid
	from collection_cb c
	inner join collection_remittance r on c.collection_objid = r.collection_objid and c.group_objid = r.group_objid
	where r.objid = $P{objid}
		and c.state = "ENCASHED"
	order by c.dtfiled 
) q 
	inner join collection_cb c on q.objid = c.objid
	inner join encashment_cbs ec on c.objid = ec.refid
	inner join encashment_check eck on ec.parentid = eck.objid
	inner join checkaccount ca on eck.objid = ca.refid
	left join depositslip_check dsc on ca.objid = dsc.refid
	left join depositslip ds on dsc.parentid = ds.objid
union 
select rd.objid, ds.controlno as cbsno, rd.borrower_name as description, 
	rd.amount as cashremitted, 
	case
		when a.loantype = "branch" then "branch"
		when a.loantype <> "branch" then "check"
	end as type
from collection_remittance_detail rd
inner join loanapp a on rd.loanapp_objid = a.objid
inner join onlinecollection_detail ocd on rd.refid = ocd.objid
inner join checkpayment cp on ocd.objid = cp.refid
inner join checkaccount ca on cp.objid = ca.objid
left join depositslip_check dsc on ca.objid = dsc.refid
left join depositslip ds on dsc.parentid = ds.objid
where rd.parentid = $P{objid}

[getOverageByDate]
select r.collector_name as collector, o.amount,
	CASE 
		WHEN r.group_type = 'route' THEN lr.description
		WHEN r.group_type = 'online' THEN "DIRECT"
		ELSE UCASE(r.group_type)
	END AS route
from collection_remittance r
inner join overage o on r.objid = o.remittanceid
left join loan_route lr on r.group_objid = lr.code
where r.txndate = $P{date}
	and r.state = "POSTED"
	and o.state not in ('voided')
order by route

[getCashOnHandByDate]
select "cbs" as type, r.objid, cb.cbsno as slipno, 
	ifnull((select sum(amount) 
		from collection_cb_detail 
		where parentid = cb.objid), 0) as cashamount,
	ifnull((select sum(s.amount) 
		from shortage s
		where s.remittanceid = r.objid
			and s.state in ("noted","approved")), 0) as shortageamount,
	0 as checkamount,
	case
		when cb.group_type = 'followup' then concat("Follow-up - ", cb.collector_name)
		when cb.group_type <> 'followup' then cb.collector_name
	end as collector
from collection_remittance r
inner join collection_cb cb on r.collection_objid = cb.collection_objid and r.group_objid = cb.group_objid
where r.txndate = $P{date}
	and r.state = "posted"
	and cb.state <> "encashed"
union
select "encashment" as type, ecbs.objid, d.controlno as slipno,
	0 as cashamount, 0 as shortageamount, 0 as checkamount,
	case
		when cb.group_type = 'followup' then concat("Follow-up - ", cb.collector_name, " - Encashment")
		when cb.group_type <> 'followup' then concat(cb.collector_name, " - Encashment")
	end as collector
from depositslip d
inner join depositslip_check dsc on d.objid = dsc.parentid
inner join checkaccount ca on dsc.refid = ca.objid
inner join encashment e on ca.refid = e.objid
inner join encashment_cbs ecbs on e.objid = ecbs.parentid
inner join collection_cb cb on ecbs.refid = cb.objid
where e.txndate = $P{date}
	and e.txnstate = "approved"
union
select 
	case 
		when a.loantype = "branch" then "brach"
		when a.loantype <> "branch" then "check"
	end as type, r.objid, ds.controlno as slipno, 0 as cashamount,
	0 as shortageamount, sum(ca.amount) as checkamount, oc.collector_name as collector
from collection_remittance r
inner join collection_remittance_detail d on r.objid = d.parentid
inner join loanapp a on d.loanapp_objid = a.objid
inner join onlinecollection oc on r.collection_objid = oc.objid
inner join onlinecollection_detail ocd on oc.objid = ocd.parentid
inner join checkpayment cp on ocd.objid = cp.refid
inner join checkaccount ca on cp.objid = ca.objid
left join depositslip_check dsc on ca.objid = dsc.refid
left join depositslip ds on dsc.parentid = ds.objid
where r.txndate = $P{date}
	and r.state = "posted"
group by collector, type, slipno, r.objid