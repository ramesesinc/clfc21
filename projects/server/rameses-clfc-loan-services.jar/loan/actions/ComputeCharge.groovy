package loan.actions;

import com.rameses.rules.common.*;
import com.rameses.util.*;
import java.rmi.server.UID;

public class ComputeCharge implements RuleActionHandler {
	def NS;
	def IA;
	def request;
	public void execute(def params, def drools) {
		//if (!app.charges) app.charges = [];
		def APP = params.app;
		if (!APP.charges) APP.charges = [];

		def acctid = params.account.key;
		def item = IA.read([objid: acctid]);
		if (item) {
			def info = [
				objid 	: 'LCHRG' + new UID(),
				acctid	: acctid,
				title 	: item.title,
				amount 	: NS.round(params.amount.decimalValue)
			];

			APP.charges << info;
			request.charges << info
			//app.charges << info;
		}
	}
}