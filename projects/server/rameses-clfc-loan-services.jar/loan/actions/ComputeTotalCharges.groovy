package loan.actions;

import com.rameses.rules.common.*;
import com.rameses.util.*;

public class ComputeTotalCharges implements RuleActionHandler {

	def NS;

	public void execute(def params, def drools) {
		def APP = params.app;

		def total = 0;
		if (APP.charges) total = APP.charges.amount.sum();

		APP.totalcharges = total;
	}
}