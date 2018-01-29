package loan.branch.actions;

import com.rameses.rules.common.*;
import com.rameses.util.*;
import java.text.SimpleDateFormat;

public class PostItem implements RuleActionHandler {
	def NS;
	private def DATE_FORMAT = new SimpleDateFormat('yyyy-MM-dd');

	public void execute(def params, def drools) {
		def action = params.action;
		def flag = action.booleanValue;
		
		if (flag == true) {
			def seqitem = params.seqitem;
			String fn = seqitem?.name;
			def val = params.values[fn];
			//val = NS.round(val);
			params.item[fn] = val;
			params.isPosted = true;

			//println 'fn ' + fn + ' bindings ' + params.bindings;

			switch (fn) {
				case 'interest': 
					val = NS.round(val);
					params.PAYMENT.totalInterest += val;
					params.item[fn + "paid"] = val;
					break;
				case 'penalty':
					val = NS.round(val);
					params.PAYMENT.totalPenalty += val; 
					params.item[fn + "paid"] = val;
					break; 
			}

			fn = "lacking" +  fn;
			if (params.values[fn] != null) {
				params.item[fn] = params.values[fn];
			}

			switch (seqitem?.datatype) {
				case 'decimal' 	: decimalProcess(params, val); break;
				case 'date' 	: dateProcess(params); break;
			}
		}
	}

	void decimalProcess( params, value ) {
		if (params.seqitem?.isdeductabletoamount == true) {
			def PAYMENT = params.PAYMENT
			def val = PAYMENT.amountPaid - value;
			val = NS.round(val);
			PAYMENT.amountPaid = val;
			//println 'value ' + value + ' amount paid ' + PAYMENT.amountPaid;
		}

		def headers = params.headers;
		if (params.seqitem?.name == 'interest') {
			def i = headers.find{ it.code == 'SCHEDULE_DATE' }
			if (!i) {
				incrementCurrentSchedule(params);
			}
		}
	}

	void dateProcess( params ) {
		if (params.seqitem?.isincrementafterposting == true) {
			incrementCurrentSchedule(params);
			//println 'current schedule ' + bindings.CURRENT_SCHEDULE;
		}
	}
	
	void incrementCurrentSchedule( params ) {
		def bindings = params.bindings;
		def currentschedule = bindings.CURRENT_SCHEDULE;
		//println 'current schedule ' + currentschedule;

		def cal = Calendar.getInstance();
		cal.setTime(parseDate(currentschedule));
		def term = params.postingterm;

		switch (term) {
			case 'ANNUAL' 	: cal.add(Caledar.YEAR, 1); break;
			case 'MONTHLY' 	: cal.add(Calendar.MONTH, 1); break;
			default 		: cal.add(Calendar.DATE, 1); break;
		}

		//bindings.LAST_SCHEDULE = bindings.CURRENT_SCHEDULE;
		bindings.CURRENT_SCHEDULE = parseDate(DATE_FORMAT.format(cal.getTime()));
		bindings.INTERESTPAID = 0;
	}

	def parseDate( date ) {
		if (date == null) return null;

		if (date instanceof Date) {
			return date;
		} else {
			return java.sql.Date.valueOf(date);
		}
	}
}
	