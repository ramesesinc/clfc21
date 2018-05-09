[getListByParent]
select * from ( 
	select 'business' as reftype, tradename as refname, ci_evaluation as evaluation
	from loanapp_business 
	where parentid = $P{parentid} 
	union all 
	select 'collateral' as reftype, reftype as refname, evaluation 
	from loanapp_cireport 
	where parentid = $P{parentid} 
)tmp1 
