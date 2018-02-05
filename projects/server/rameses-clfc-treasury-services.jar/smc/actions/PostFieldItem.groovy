package smc.actions;

import com.rameses.rules.common.*;
import com.rameses.util.*;

public class PostFieldItem implements RuleActionHandler {
	def NS;
	public void execute(def params, def drools) {
		def action = params.action;
		def item = params.item;
		def val;
		switch (params.datatype) {
			case 'decimal'	: val = NS.round(action.decimalValue); break;
			case 'integer' 	: val = action.intValue; break;
			case 'double' 	: val = NS.round(action.doubleValue); break;
			case 'boolean' 	: val = action.booleanValue; break;
			default 		: val = action.stringValue; break;
		}

		item.value = val;
	}
}