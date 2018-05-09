[findPending]
select b.* 
from mqpub_pending p 
	inner join mqpub a on a.objid = p.objid 
	inner join mqpubitem b on (b.parentid = a.objid and b.indexno = p.indexno ) 
order by a.dtfiled 
