package smc.actions;

import com.rameses.rules.common.*;
import com.rameses.util.*;

public class OffsetFieldValue implements RuleActionHandler {
	def NS;
	def regex = '(integer|decimal)'
	public void execute(def params, def drools) {
		def action = params.action;

		def flag = action.booleanValue;
		if (flag == true) {
			def fn = params.seqitem?.name;
			def val = params.lastitem[fn];
			if (params.seqitem?.datatype?.matches(regex) && val > 0) {
				params.item[fn] = val * -1;	
				if (params.bindings["LACKING" + fn.toUpperCase()]) {
					params.bindings["LACKING" + fn.toUpperCase()] = 0;
				}
			}

			if (params.seqitem?.isdeductabletoamount == true) {
				params.bindings?.AMOUNT += val;
				params.PAYMENT.amountPaid += val;
				switch (fn) {
					case 'interest'	: params.PAYMENT.totalInterest -= val; break;
					case 'penalty' 	: params.PAYMENT.totalPenalty -= val; break; 
				}
			}
		}
	}
}