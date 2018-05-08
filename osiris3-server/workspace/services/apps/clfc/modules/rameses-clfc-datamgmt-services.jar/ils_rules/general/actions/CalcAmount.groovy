package ils_rules.general.actions;

import com.rameses.rules.common.*;
import com.rameses.util.*;

public class CalcAmount implements RuleActionHandler {
	def NS;
	def data;
	public void execute(def params, def drools) {
		def key = params.key;
		def amt = NS.round(params[key].decimalValue);
		if (data != null) {
			data[key] = amt;
		}
	}
}
	