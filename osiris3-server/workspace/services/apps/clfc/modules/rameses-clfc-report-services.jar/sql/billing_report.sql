[getBillingByDate]
select b.objid as billingid, b.collector_objid, b.collector_name, b.billdate as date,
	i.objid as itemid, i.state, i.item_objid, i.item_type,
	case
		when i.item_type = 'route' then r.description
		when i.item_type = 'followup' then 'Follow-up'
		when i.item_type = 'special' then 'Special'
	end as description
from ledger_billing b
inner join ledger_billing_item i on b.objid = i.parentid
left join loan_route r on i.item_objid = r.code
where b.billdate = $P{date}
order by description

[getBillingByStartDateAndEndDate]
select b.objid as billingid, b.collector_objid, b.collector_name, b.billdate,
	i.objid as itemid, i.state, i.item_objid, i.item_type,
	case
		when i.item_type = 'route' then r.description
		when i.item_type = 'followup' then 'Follow-up'
		when i.item_type = 'special' then 'Special'
	end as description
from ledger_billing b
inner join ledger_billing_item i on b.objid = i.parentid
left join loan_route r on i.item_objid = r.code
where b.billdate between $P{startdate} and $P{enddate}
order by b.billdate

[getBillingDetail]
select d.*, l.appid
from ledger_billing_detail d
left join loan_ledger l on d.ledgerid = l.objid
where d.parentid = $P{objid}
order by d.refno

[getFieldPayments]
select p.*
from fieldcollection_payment p
where p.parentid = $P{objid}