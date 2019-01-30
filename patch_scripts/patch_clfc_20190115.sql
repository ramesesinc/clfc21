alter table loanapp_collateral_appliance
add column `attachments` text default null after `remarks`
;
alter table loanapp_collateral_other
add column `attachments` text default null after `remarks`
;
alter table loanapp_collateral_property
add column `attachments` text default null after `remarks`
;
alter table loanapp_collateral_vehicle
add column `attachments` text default null after `orcr_shippingwt`
;