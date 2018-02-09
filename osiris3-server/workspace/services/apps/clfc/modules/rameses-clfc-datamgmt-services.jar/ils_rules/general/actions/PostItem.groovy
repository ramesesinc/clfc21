package ils_rules.general.actions;

import com.rameses.rules.common.*;
import com.rameses.util.*;
import java.text.SimpleDateFormat;

public class PostItem implements RuleActionHandler {
	def NS;
	private def DATE_FORMAT = new SimpleDateFormat('yyyy-MM-dd');

	public void execute( def params, def drools ) {
		def postitem = params.postitem.booleanValue;

		if (postitem == true) {
			def postingitem = params.postingitem;
			def listitem = params.listitem;
			def values = params.values;

			//params.isposted = true;

			postingitem[listitem.name] = values[listitem.name];
			//println 'item-> ' + listitem.name + ' value-> ' + values[listitem.name];

			switch (listitem.datatype) {
				case 'decimal'	: decimalProcess( params, listitem ); break;
				case 'date'		: dateProcess( params, listitem ); break;
			}

			//params.isPosted = true;
			//println 'posting item-> ' + postingitem;

		}


		//throw new RuntimeException("stop posting");


	}

	void decimalProcess( params, item ) {
		//println 'item ' + item;
		if (item.isdeductabletoamount == true) {
			def postingamount = params.values[item.name];

			if (!params.PAYMENT.deductableToAmount) params.PAYMENT.deductableToAmount = 0;
			def paymentamount = params.PAYMENT.amount - params.PAYMENT.deductableToAmount;

			if (!params.PAYMENT.totalPaid[item.name]) params.PAYMENT.totalPaid[item.name] = 0;
			if (!params.PAYMENT.total[item.name]) params.PAYMENT.total[item.name] = 0;
			if (!params.PAYMENT.lacking[item.name]) params.PAYMENT.lacking[item.name] = 0;

			params.PAYMENT.total[item.name] += postingamount;

			if (paymentamount >= postingamount) {
				//params.PAYMENT.amount -= postingamount;
				params.PAYMENT.deductableToAmount += postingamount;
				params.PAYMENT.totalPaid[item.name] += postingamount;
				params.postingitem[item.name] = postingamount;
				//params.PAYMENT.total[item.name] += postingamount;
			} else {
				def lackingamount = postingamount - paymentamount;

				//params.PAYMENT.amount -= paymentamount;
				params.PAYMENT.deductableToAmount += paymentamount;
				params.PAYMENT.totalPaid[item.name] += paymentamount;
				params.postingitem[item.name] = paymentamount;
				//params.PAYMENT.total[item.name] += paymentamount;
				if (lackingamount > 0) {
					if (params.totaldays > 1 && item.recalculateifnotenough == true) {
						params.allowRepost = true;
					}
					params.PAYMENT.lacking[item.name] += lackingamount;
				}
			}

			//println 'payment ' + params.PAYMENT;
		}
	}

	void dateProcess( params, item ) {
		if (item.isincrementafterposting == true) {
			if (!params.incrementAfterPosting[item.name]) params.incrementAfterPosting[item.name] = 0;
			params.incrementAfterPosting[item.name] += 1;
			//params.PAYMENT.addToCurrentSchedule += 1;
		}
	}

	public void xexecute(def params, def drools) {
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

	void xdecimalProcess( params, value ) {
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

	void xdateProcess( params ) {
		if (params.seqitem?.isincrementafterposting == true) {
			incrementCurrentSchedule(params);
			//println 'current schedule ' + bindings.CURRENT_SCHEDULE;
		}
	}

	void xincrementCurrentSchedule( params ) {
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