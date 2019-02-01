alter table loanapp_otherlending drop column remarks;
alter table loanapp_otherlending change collateral otherinfo text;

