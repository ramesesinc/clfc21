package loan.actions;

import com.rameses.rules.common.*;
import com.rameses.util.*;
import loan.facts.*;

public class CalculateLoanInterest implements RuleActionHandler {
	def NS, LS;
	public void execute(def params, def drools) {
		def APP = params.app;
		APP.interest = LS.roundOffAmount(NS.round(params.amount.decimalValue));
	}

}