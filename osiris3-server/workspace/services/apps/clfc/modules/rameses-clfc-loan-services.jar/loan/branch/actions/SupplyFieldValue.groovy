package loan.branch.actions;

import com.rameses.rules.common.*;
import com.rameses.util.*;

public class SupplyFieldValue implements RuleActionHandler {
	def NS;
	public void execute(def params, def drools) {
		def action = params.action;

		def seqitem = params.seqitem;
		def fn = seqitem.name;
		def item = params.item;
		def val;
		switch (seqitem.datatype) {
			case 'decimal'	: val = NS.round(action.decimalValue); break;
			case 'integer' 	: val = action.intValue; break;
			case 'double' 	: val = NS.round(action.doubleValue); break;
			case 'boolean' 	: val = action.booleanValue; break;
			case 'date' 	: val = java.sql.Date.valueOf(action.stringValue); break;
			default 		: val = action.stringValue; break;
		}

		params.values[fn] = val;

		if (seqitem.isdeductabletoamount == true) {
			def bindings = params.bindings;

			if (bindings["LACKING" + fn.toUpperCase()] != null) {
				bindings["LACKING" + fn.toUpperCase()] = 0;
			}

			if (bindings?.AMOUNT >= val) {
				bindings?.AMOUNT -= val;
			} else {
				params.values[fn] = bindings?.AMOUNT;
				val -= bindings?.AMOUNT;
				bindings?.AMOUNT = 0;
				params.values["lacking" +fn] = val;
				bindings["LACKING" + fn.toUpperCase()] = val;
			}
		}
		//println 'fn ' + fn + ' val ' + params.values[fn];
	}
}