package loan.actions;

import com.rameses.rules.common.*;

class ComputeNetAmount implements RuleActionHandler {

	def NS;
	public void execute(def params, def drools) {
		def APP = params.app;
		APP.netamount = NS.round(params.amount.decimalValue);
	}
}