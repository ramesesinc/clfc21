package loan.branch.actions;

import com.rameses.rules.common.*;
import com.rameses.util.*;
import loan.facts.*;

public class CalcAmount implements RuleActionHandler {
	def NS;
	def data;
	public void execute(def params, def drools) {
		def amt = NS.round(params.amount.decimalValue);
		data?.amount = amt;
		params.amount = amt;
	}
}
	