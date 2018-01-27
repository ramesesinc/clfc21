package smc.actions;

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
			def fieldname = params.fieldname;
			def val = params.values[fieldname];
			params.item[fieldname] = val;
			params.isPosted = true;

			switch (fieldname) {
				case 'interest' : 
					val = NS.round(val);
					params.PAYMENT.totalInterest += val;
					params.item[fieldname + "paid"] = val;
					break;
				case 'penalty'	: 
					val = NS.round(val);
					params.PAYMENT.totalPenalty += val; 
					params.item[fieldname + "paid"] = val;
					break; 
			}

			fieldname = "lacking" + fieldname;
			if (params.values[fieldname] != null) {
				params.item[fieldname] = params.values[fieldname];
			}

			switch (params.seqitem?.datatype) {
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
			case 'ANNUAL' 		: cal.add(Caledar.YEAR, 1); break;
			case 'SEMI-ANNUAL' 	: cal.add(Calendar.MONTH, 6); break;
			case 'MONTHLY' 		: cal.add(Calendar.MONTH, 1); break;
			case 'SEMI-MONTHLY' : def d = cal.get(Calendar.DATE);
								  if (d <= 15) {
								  	cal.set(Calendar.DATE, 15);
								  } else if (d > 15) {
								  	cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
								  }
								  break;
			case 'WEEKLY' 		: cal.add(Calendar.DATE, 7); break;
			default 			: cal.add(Calendar.DATE, 1); break;
		}

		//println 'current schedule ' + bindings.CURRENT_SCHEDULE;
		bindings.CURRENT_SCHEDULE = parseDate(DATE_FORMAT.format(cal.getTime()));
		bindings.INTERESTPAID = 0;
		//println '**current schedule ' + bindings.CURRENT_SCHEDULE;
		//println '**values ' + params.values;
		//println 'current schedule2 ' + bindings.CURRENT_SCHEDULE;
		//println '';
	}

	def parseDate( date ) {
		if (!date) return null;

		if (date instanceof Date) {
			return date;
		} else {
			return java.sql.Date.valueOf(date);
		}
	}

	/*
	public void execute(def params, def drools) {
		def action = params.action;
		def flag = action.booleanValue;		

		//println 'allow update ' + params.allowUpdate;

		if (!data.counter) data.counter = 0;
		if (data.counter < 1) {
			flag = true;
			data.counter++;
		}

		params.allowUpdate = flag;


		switch (flag) {
			case true	: addItem(params); break;
			case false 	: addLastItemData(params); break;
		}
	}

	def getLastItem() {
		if (!data.items) data.items = [];
		if (data?.items?.size() == 0) data?.items << [:];
		return data?.items[data?.items.size() - 1];
	}

	void addItem( params ) {
		//println 'add item';

		def list = postingitems?.findAll{ it.datatype == 'decimal' }
		println 'headers';
		list?.each{ println it }

		def vars = [:];
		def facts = params.facts;

		facts?.each{ o->
			vars[o.getClassName().toLowerCase()] = o.getInfo();
		}

		println 'vars';
		vars?.each{ println it }


	}

	void addLastItemData( params ) {
		def lastitem = getLastItem();
		def list = conditions?.findAll{ it.postonlastitem == true }
		list?.each{ o->
			lastitem[o.name] = 'test value';
		}

		//println 'last item ';
		//lastitem?.each{ println it }
	}
	*/
}