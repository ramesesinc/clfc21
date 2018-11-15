package ils_rules.general.actions;

import com.rameses.rules.common.*;
import com.rameses.util.*;

public class CalcDate implements RuleActionHandler {
	def data;
	def DS;
	public void execute(def params, def drools) {
		def key = params.key;

		if (params[key]) {
			def val = params[key].eval();
			def dt = parseDate("yyyy-MM-dd", val);
			if (data != null) {
				data[key] = dt;
			}
		}
	}

	private def parseDate( date ) {
		if (!date) return null;

		if (date instanceof Date) {
			return date;
		} else {
			return java.sql.Date.valueOf( date );
		}
	}

	private def parseDate( def pattern, def date ) {
		if (!pattern) pattern = "yyyy-MM-dd";

		return parseDate(new java.text.SimpleDateFormat(pattern).format(parseDate(date)));
	}

}
	