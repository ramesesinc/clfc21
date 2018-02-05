package ils_rules.general.actions;

import com.rameses.rules.common.*;
import com.rameses.util.*;
import java.text.*;

public class SupplyFieldValue implements RuleActionHandler {
	def NS;

	public void execute( def params, def drools ) {
		def key = params.key;
		def datatype = params.datatype;
		def action = params[key];

		if (!params.bindings) params.bindings = [:];

		def val;
		switch (datatype) {
			case 'decimal'	: val = NS.round( action.decimalValue ); break;
			case 'integer' 	: val = action.intValue; break;
			case 'double' 	: val = NS.round(action.doubleValue); break;
			case 'boolean' 	: val = action.booleanValue; break;
			case 'date' 	: 
				val = java.sql.Date.valueOf(action.stringValue);
				if (params.incrementAfterPosting[key]) {
					Calendar cal = Calendar.getInstance();
					cal.setTime(val);
					cal.add(Calendar.DATE, params.incrementAfterPosting[key]);
					val = parse("yyyy-MM-dd", cal.getTime());
				}
				break;
			default 		: val = action.stringValue; break;
		}


		//println 'val-> ' + val;
		if (key == 'penalty') {
			//println key + '-> ' + val;
		}

		params.values[key] = val;

		if (params.listitem?.varname) {
			params.bindings[params.listitem.varname] = val;
		}
		//println 'bindings--> ' + params.bindings;
	}

	def parse( def pattern, def date ) {
		if (!pattern) pattern = "yyyy-MM-dd";

		return parse(new SimpleDateFormat(pattern).format(parse(date)));
	}

	def parse( def date ) {
		if (!date) return null;

		if (date instanceof Date) {
			return date;
		} else {
			return java.sql.Date.valueOf(date);
		}
	}


	/*
	public void xexecute(def params, def drools) {
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
	*/
}