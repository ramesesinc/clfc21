<% data.items.each { o-> %> 
<font class="bold">${o.reftype.toString().toUpperCase()}: ${o.refname.toString().toUpperCase()}</font>
<br/>
<pre>${o.evaluation}</pre>
<br/> 
<% } %> 
