package ils_rules.general.actions;

import com.rameses.rules.common.*;
import com.rameses.util.*;

public class CalcDate implements RuleActionHandler {
	def data;
	def DS;
	public void execute(def params, def drools) {
		def key = params.key;
		def dt = java.sql.Date.valueOf(params[key].stringValue);
		if (data != null) {
			data[key] = dt;
		}
	}
}
	