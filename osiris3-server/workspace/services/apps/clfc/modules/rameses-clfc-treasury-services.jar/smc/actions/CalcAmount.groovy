package smc.actions;

import com.rameses.rules.common.*;
import com.rameses.util.*;

public class CalcAmount implements RuleActionHandler {
	def NS;
	def data;

	public void execute(def params, def drools) {
		data.amount = NS.round(params.amount.decimalValue);
	}
}