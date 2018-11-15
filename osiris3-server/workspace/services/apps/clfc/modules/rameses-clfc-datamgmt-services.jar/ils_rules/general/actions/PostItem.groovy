package ils_rules.general.actions;

import com.rameses.rules.common.*;
import com.rameses.util.*;
import java.text.SimpleDateFormat;

public class PostItem implements RuleActionHandler {
	def NS, SERVICE;
	private def DATE_FORMAT = new SimpleDateFormat('yyyy-MM-dd');

	public void execute( def params, def drools ) {
		def postitem = params.postitem.booleanValue;
		def listitem = params.listitem;

		/*
		if (listitem.name.matches('underpaymentpenalty2')) {
			println 'item->' + listitem.name + ' values->' + params.values;
		}
		*/
		//println 'postitem-> ' + postitem + ' item->' + listitem;

		//postitem = false;
		//params.postingitem?.allowpost = false;
		if (postitem == true && listitem.header) {
			def postingitem = params.postingitem;
			def values = params.values;
			def header = listitem.header;
			//def header = listitem.posttoheader;

			//println 'header ' + header;
			//println 'values ' + values;

			//params.isposted = true;
			//println 'values->' + values;
			if ( values[header.name] ) {
				postingitem[header.name] = values[header.name];
				postingitem.allowpost = true;
				params.PAYMENT.current[header.name] = values[header.name];
			}
			if (listitem?.datatype?.matches( 'decimal' )) {
				postingitem?.remove( header.name );
				postingitem?.allowpost = false;
				params.PAYMENT.current?.remove( header.name );
			}
			//println 'datatype->' + listitem.datatype;
			//println 'item->' + listitem.name;
			//println 'item-> ' + listitem.name + ' value-> ' + values[listitem.name];
			switch ( listitem.datatype ) {
				case 'decimal': 
					decimalProcess( params, listitem, header ); 
					break;
				case 'date': 
					dateProcess( params, listitem, header ); 
					break;
				default:
					break;
			}

			if (params.islastitem == true) {
				params.postingitem.allowpost = true;
			}

			//println 'is last item->' + params.islastitem + ' posting item allow post->' + params.postingitem.allowpost;

			//params.isPosted = true;
			//println 'posting item-> ' + postingitem;

		}

		//println 'posting item allow post->' + params?.postingitem?.allowpost + ' ' + params?.listitem?.name;
		//println 'allow repost->' + params.allowRepost;

		//throw new RuntimeException("stop posting");


	}

	void decimalProcess( params, item, header ) {
		//println 'item ' + item;
		//println '';
		if (!params.PAYMENT.amount) params.PAYMENT.amount = 0;
		if (!params.PAYMENT.deductableToAmount) params.PAYMENT.deductableToAmount = 0;

		def totalkey = "total_" + item.name + "f";
		def paidkey = "paid_" + item.name + "f";
		def savekey = "save_" + item.name + "f";

		//println 'listitem->' + item;
		def allowpost = true;

		//println 'item->' + item.name;
		if (!item.allowrepeat && item.isdeductabletoamount == true && params.loaninfo != null && header != null) {
			//println 'header->' + header;
			//println 'loaninfo->' + params.loaninfo;

			if (!params.loaninfo[paidkey]) params.loaninfo[paidkey] = 0;
			if (!params.loaninfo[totalkey]) params.loaninfo[totalkey] = 0;

			if (params.loaninfo[paidkey] <= 0 || params.loaninfo[paidkey] < params.loaninfo[totalkey]) {
				if (!params.loancopy.info[totalkey]) params.loancopy.info[totalkey] = 0;

				if (params.postingitem[totalkey]) {
					params.loaninfo[totalkey] += params.postingitem[totalkey];
					//params.loancopy.info[totalkey] += params.postingitem[totalkey];
				}

				//println 'totalkey1->' + params.loaninfo[totalkey] + ' totalkey2->' + params.loancopy.info[totalkey];

				if (params.PAYMENT.lacking) {
					if (params.PAYMENT.lacking[item.name]) {
						params.loaninfo[totalkey] -= params.PAYMENT.lacking[item.name];
						//params.loancopy.info[totalkey] -= amt;
					}
				}
				//println 'totalkey3->' + params.loaninfo[totalkey] + ' totalkey4->' + params.loancopy.info[totalkey];


				//if (params.postingitem[totalkey] && !params.loaninfo[totalkey]) {
				//	params.loaninfo[totalkey] = params.postingitem[totalkey];
				//}

				if (!params.loaninfo[totalkey]) params.loaninfo[totalkey] = 0;
				if (!params.loaninfo[paidkey]) params.loaninfo[paidkey] = 0;

				//println 'values->' + params.values;
				//println 'loaninfo->' + params.loaninfo;
				//println 'paid->' + params.loaninfo[paidkey] + ' total->' + params.loaninfo[totalkey];

				//println 'lacking1->' + params.PAYMENT.lacking;

				if (params.loaninfo[paidkey] >= params.loaninfo[totalkey]) {
					allowpost = false;
				}

				if (allowpost == true) {
					if (!params.loancopy.info[totalkey]) params.loancopy.info[totalkey] = 0;
					if (params.postingitem[totalkey]) {
						params.loancopy.info[totalkey] += params.postingitem[totalkey];						
					}
					if (params.PAYMENT.lacking[item.name] && params.loancopy.info[totalkey] > 0) {
						params.loancopy.info[totalkey] -= params.PAYMENT.lacking[item.name];
					}
				}
			} else {
				allowpost = false;
			}
		}
		//println 'payment->' + params.PAYMENT;

		//println 'postingitem->' + params.postingitem;
		//println 'allow posting->' + allowpost + ' payment amount->' + paymentamount + ' amount->' + params.PAYMENT.amount + ' deductable to amount->' + params.PAYMENT.deductableToAmount;
		//println 'isdeductabletoamount->' + item.isdeductabletoamount + ' values->' + params.values[item.name] + ' header->' + header;
		//println 'allow posting->' + allowpost + ' item->' + item.name + ' payment amount->' + paymentamount + ' amount->' + params.PAYMENT.amount + ' deductable to amount->' + params.PAYMENT.deductableToAmount;
		//println 'values->' + params.values;
		//println 'deductable to amount->' + item.isdeductabletoamount + ' posting item->' + params.postingitem + ' payment amount->' + paymentamount + ' name->' + header?.name;

		//println 'paidkey->' + paidkey + ' val->' + params.loaninfo[paidkey];
		//println 'totalkey->' + totalkey + ' val->' + params.loaninfo[totalkey];
		//println 'allow posting->' + allowpost + ' item->' + item.name + ' values->' + params.values;
		//println 'allow repeat->' + item.allowrepeat + ' paidkey->' + params.loaninfo[paidkey] + ' totalkey->' + params.loaninfo[totalkey];
		//println 'item->' + item.name + ' values->' + params.values;
		//println 'loaninfo->' + params.loaninfo;
		//println 'allow posting->' + allowpost + ' item->' + item.name;
		//println 'paidkey1->' + params.loaninfo[paidkey] + ' totalkey1->' + params.loaninfo[totalkey];
		//println 'paidkey2->' + params.loancopy.info[paidkey] + ' totalkey2->' + params.loancopy.info[totalkey];
		//println 'lacking->' + params.PAYMENT.lacking;


		//if (item.group == 'OVER') {
		//	println 'allowpost->' + allowpost + ' item->' + item.name;
		//}

		if (allowpost == true && header != null && params.values[item.name]) {
			if (!params.postingitem[header.name]) params.postingitem[header.name] = 0;

			params.allowRepost = false;
			def postingamount = params.values[item.name];
			
			def paymentamount = params.PAYMENT.amount - params.PAYMENT.deductableToAmount;
			//println 'deductable to amount->' + item.isdeductabletoamount + ' payment amount->' + paymentamount;
			//println 'item->' + item.name + ' header->' + header.name + ' values->' + params.values;
			if (item.isdeductabletoamount == true && paymentamount > 0) {
			
				if (!params.PAYMENT.totalPaid[header.name]) params.PAYMENT.totalPaid[header.name] = 0;
				if (!params.PAYMENT.totalPaid[item.name]) params.PAYMENT.totalPaid[item.name] = 0;

				if (!params.PAYMENT.total[header.name]) params.PAYMENT.total[header.name] = 0;
				if (!params.PAYMENT.total[item.name]) params.PAYMENT.total[item.name] = 0;

				if (!params.postingitem[paidkey]) params.postingitem[paidkey] = 0;

				if (params.postingitem[totalkey] && !params.loaninfo[totalkey]) {
					params.loaninfo[totalkey] = params.postingitem[totalkey];
				}

				if (!params.loaninfo[paidkey]) params.loaninfo[paidkey] = 0;
				if (!params.loaninfo[totalkey]) params.loaninfo[totalkey] = 0;
				if (!params.loancopy.info[paidkey]) params.loancopy.info[paidkey] = 0;

				if (!params.PAYMENT.lacking[header.name]) params.PAYMENT.lacking[header.name] = 0;
				if (!params.PAYMENT.lacking[item.name]) params.PAYMENT.lacking[item.name] = 0;

				//println 'item->' + item.name;
				//println 'total->' + params.PAYMENT.total;
				//println 'posting amount->' + postingamount;

				//println 'item->' + item.name + ' post with lacking->' + item.postwithlacking;

				if (item.postwithlacking == true) {

					def amt = 0, lackingamount = 0;
					if (paymentamount >= postingamount) {
						amt = postingamount;
					} else {
						lackingamount = postingamount - paymentamount;
						amt = paymentamount;
					}

					def varamt = amt;
					if (item.deductabletoamountusevar == true) {
						if (params.bindings[item.deductabletoamountvar]) {
							varamt = params.bindings[item.deductabletoamountvar];
						}
					}
					
					/*
					if (params.ADVANCE_POSTING) {
						println 'amount1->' + params.ADVANCE_POSTING.amount;
						params.ADVANCE_POSTING.amount -= varamt;
						println 'amount2->' + params.ADVANCE_POSTING.amount;
					}
					*/

					if (item.overridevalue == true) {
						params.PAYMENT.deductableToAmount = varamt;
						params.PAYMENT.totalPaid[header.name] = amt
						params.PAYMENT.totalPaid[item.name] = amt;

						params.PAYMENT.total[header.name] = amt
						params.PAYMENT.total[item.name] = amt;

						params.postingitem[header.name] = amt;
						params.postingitem[paidkey] = amt;
						params.loaninfo[paidkey] = amt;
						params.loancopy.info[paidkey] = amt;
						if (lackingamount > 0) {
							if (item.recalculateifnotenough == true) {
								params.allowRepost = true;
							}
							params.PAYMENT.lacking[header.name] = lackingamount;
							params.PAYMENT.lacking[item.name] = lackingamount;
						}
					} else {
						params.PAYMENT.deductableToAmount += varamt;
						params.PAYMENT.totalPaid[header.name] += amt
						params.PAYMENT.totalPaid[item.name] += amt;

						params.PAYMENT.total[header.name] += amt
						params.PAYMENT.total[item.name] += amt;

						params.postingitem[header.name] += amt;
						params.postingitem[paidkey] += amt;
						params.loaninfo[paidkey] += amt;
						params.loancopy.info[paidkey] += amt;
						if (lackingamount > 0) {
							if (item.recalculateifnotenough == true) {
								params.allowRepost = true;
							}
							params.PAYMENT.lacking[header.name] += lackingamount;
							params.PAYMENT.lacking[item.name] += lackingamount;
						}
					}
					//println 'item->' + item.name + ' lacking amount->' + lackingamount;

					if (lackingamount == 0) {
						params.PAYMENT.lacking.remove( header.name );
						params.PAYMENT.lacking.remove( item.name );
						//params.PAYMENT.lacking[header.name] = lackingamount;
						//params.PAYMENT.lacking[item.name] = lackingamount;
					}

					if (item.allowrepeat == true && params.loaninfo[totalkey] && params.loaninfo[paidkey] >= params.loaninfo[totalkey]) {
						params.loaninfo.remove( paidkey );
						params.loaninfo.remove( totalkey );

						params.loancopy.info.remove( paidkey );
						params.loancopy.info.remove( totalkey );
					}


					params.PAYMENT.current[header.name] = params.postingitem[header.name];
					params.postingitem.allowpost = true;
					params.loaninfo[savekey] = true;
				} else {

					if (paymentamount >= postingamount) {
						params.PAYMENT.total[header.name] += postingamount
						params.PAYMENT.total[item.name] += postingamount;

						def varamt = postingamount;
						if (item.deductabletoamountusevar == true) {
							if (params.bindings[ item.deductabletoamountvar ]) {
								varamt = params.bindings[ item.deductabletoamountvar ];
							}
						}
					
						if (params.ADVANCE_POSTING) {
							println 'amount1->' + params.ADVANCE_POSTING.amount;
							params.ADVANCE_POSTING.amount -= varamt;
							println 'amount2->' + params.ADVANCE_POSTING.amount;
						}

						if (item.overridevalue == true) {
							params.PAYMENT.deductableToAmount = varamt;
							params.PAYMENT.totalPaid[header.name] = postingamount;
							params.PAYMENT.totalPaid[item.name] = postingamount;
							params.postingitem[header.name] = postingamount;
							params.postingitem[paidkey] = postingamount;
							params.loaninfo[paidkey] = postingamount;
							params.loancopy.info[paidkey] = postingamount;
						} else {
							params.PAYMENT.deductableToAmount += varamt;
							params.PAYMENT.totalPaid[header.name] += postingamount
							params.PAYMENT.totalPaid[item.name] += postingamount;
							params.postingitem[header.name] += postingamount;
							params.postingitem[paidkey] += postingamount;
							params.loaninfo[paidkey] += postingamount;
							params.loancopy.info[paidkey] += postingamount;
						}
						//println 'item->' + item.name + ' lacking amount->' + lackingamount;

						params.PAYMENT.lacking.remove( header.name );
						params.PAYMENT.lacking.remove( item.name );

						if (item.allowrepeat == true && params.loaninfo[totalkey] && params.loaninfo[paidkey] >= params.loaninfo[totalkey]) {
							params.loaninfo.remove( paidkey );
							params.loaninfo.remove( totalkey );

							params.loancopy.info.remove( paidkey );
							params.loancopy.info.remove( totalkey );
						}


						params.PAYMENT.current[header.name] = params.postingitem[header.name];
						params.postingitem.allowpost = true;
						params.loaninfo[savekey] = true;
					}

				}

				if (item.allowupdatedb == true) {
					SERVICE.updateDB( item, params.values, params.incrementAfterPosting, params.dbParams );
				}

				//println item.name + ' val->' + params.loancopy.info[item.name] + ' : ' + amt; 
				
				//println 'item->' + item.name + ' header->' + header.name + ' values->' + params.values;

			} else {
				//println 'item->' + item.name + ' header->' + header.name + ' values->' + params.values;
				//println 'payment amount->' + paymentamount + ' postingamount->' + postingamount;
				//println 'payment amount->' + paymentamount + ' values->' + params.values;
				//println 'dedutable to amount->' + params.PAYMENT.deductableToAmount;
				if (postingamount > 0) {
					params.postingitem[header.name] = postingamount;
					//params.PAYMENT.deductableToAmount += postingamount;
				} else {
					params.allowRepost = true;
				}
				
			}

		} else {
			params.allowRepost = false;
		}

		println 'amount-> ' + params.PAYMENT.amount;
		println 'deductable to amount-> ' + params.PAYMENT.deductableToAmount;
		println 'balance ' + params.PAYMENT.balance;
		params.PAYMENT.balance = params.PAYMENT.amount - params.PAYMENT.deductableToAmount;
		//println 'balance->' + params.PAYMENT.balance;
		/*
		if (allowpost == true && item.isdeductabletoamount == true && params.values[item.name] && header != null && paymentamount > 0) {
			//println 'allow posting';
			params.allowRepost = false;
			def postingamount = params.values[item.name];

			if (!params.PAYMENT.totalPaid[header.name]) params.PAYMENT.totalPaid[header.name] = 0;
			if (!params.PAYMENT.totalPaid[item.name]) params.PAYMENT.totalPaid[item.name] = 0;

			if (!params.PAYMENT.total[header.name]) params.PAYMENT.total[header.name] = 0;
			if (!params.PAYMENT.total[item.name]) params.PAYMENT.total[item.name] = 0;

			if (!params.postingitem[header.name]) params.postingitem[header.name] = 0;
			if (!params.postingitem[paidkey]) params.postingitem[paidkey] = 0;
			if (!params.loaninfo[paidkey]) params.loaninfo[paidkey] = 0;
			if (!params.loaninfo[totalkey]) params.loaninfo[totalkey] = 0;
			//println 'header ' + header.name;
			//println 'payment ' + params.PAYMENT;

			//if (!params.PAYMENT.lacking) params.PAYMENT.lacking = [];
			//if (!params.PAYMENT.lacking.find{ it.name == "lacking-" + header.name + "-" + params.PAYMENT.date }) {
			//	params.PAYMENT.lacking << [name: "lacking-" + header.name + "-" + params.PAYMENT.date, amount: 0, date: params.PAYMENT.date];
			//}

			if (!params.PAYMENT.lacking[header.name]) params.PAYMENT.lacking[header.name] = 0;
			if (!params.PAYMENT.lacking[item.name]) params.PAYMENT.lacking[item.name] = 0;

			params.PAYMENT.total[header.name] += postingamount
			params.PAYMENT.total[item.name] += postingamount;
			//println 'item->' + item;
			//println 'override value->' + item.overridevalue;

			//println 'payment amount->' + paymentamount + ' posting amount->' + postingamount;
			def amt = 0, lackingamount = 0;
			if (paymentamount >= postingamount) {
				//params.PAYMENT.amount -= postingamount;

				//params.PAYMENT.deductableToAmount += postingamount;
				//params.PAYMENT.totalPaid[header.name] += postingamount
				//params.PAYMENT.totalPaid[item.name] += postingamount;
				//params.postingitem[header.name] = postingamount;

				amt = postingamount;
				//params.PAYMENT.total[item.name] += postingamount;
			} else {
				lackingamount = postingamount - paymentamount;
				amt = paymentamount;

				//def lackingamount = postingamount - paymentamount;

				//params.PAYMENT.amount -= paymentamount;
				//params.PAYMENT.deductableToAmount += paymentamount;
				//params.PAYMENT.totalPaid[header.name] += paymentamount;
				//params.postingitem[header.name] = paymentamount;
				//params.PAYMENT.total[item.name] += paymentamount;
				//println 'params ' + params;
				//if (lackingamount > 0) {
				//	if (item.recalculateifnotenough == true) {
				//		params.allowRepost = true;
				//	}
				//	params.PAYMENT.lacking[header.name] += lackingamount;
				//}
			}

			//println 'item->' + item.name + ' override value->' + item.overridevalue;
			//println "deductable to amount->" + params.PAYMENT.deductableToAmount;
			if (item.overridevalue == true) {
				params.PAYMENT.deductableToAmount = amt;
				params.PAYMENT.totalPaid[header.name] = amt
				params.PAYMENT.totalPaid[item.name] = amt;
				params.postingitem[header.name] = amt;
				params.postingitem[paidkey] = amt;
				params.loaninfo[paidkey] = amt;
				if (lackingamount > 0) {
					if (item.recalculateifnotenough == true) {
						params.allowRepost = true;
					}
					params.PAYMENT.lacking[header.name] = lackingamount;
					params.PAYMENT.lacking[item.name] = lackingamount;
				}
			} else {
				params.PAYMENT.deductableToAmount += amt;
				params.PAYMENT.totalPaid[header.name] += amt
				params.PAYMENT.totalPaid[item.name] += amt;
				params.postingitem[header.name] += amt;
				params.postingitem[paidkey] += amt;
				params.loaninfo[paidkey] += amt;
				if (lackingamount > 0) {
					if (item.recalculateifnotenough == true) {
						params.allowRepost = true;
					}
					params.PAYMENT.lacking[header.name] += lackingamount;
					params.PAYMENT.lacking[item.name] += lackingamount;
				}
			}

			//println 'amount-> ' + params.PAYMENT.amount + ' deductable to amount->' + params.PAYMENT.deductableToAmount + ' lacking->' + params.PAYMENT.lacking;

			//println 'lacking amount->' + lackingamount + ' item->' + item.name;
			if (lackingamount == 0) {
				params.PAYMENT.lacking[header.name] = lackingamount;
				params.PAYMENT.lacking[item.name] = lackingamount;
			}

			//println 'postingamount->' + postingamount + ' paymentamount->' + paymentamount + ' lacking amount->' + lackingamount;

			//println 'paid->' + params.loaninfo[paidkey] + ' total->' + params.loaninfo[totalkey];
			if (params.loaninfo[totalkey] && params.loaninfo[paidkey] >= params.loaninfo[totalkey]) {
				params.loaninfo.remove( paidkey );
				params.loaninfo.remove( totalkey );
			}


			params.PAYMENT.current[header.name] = params.postingitem[header.name];
			params.postingitem.allowpost = true;
			//println 'allow repost->' + params.allowRepost;
			//println 'payment ' + params.PAYMENT;
		} else {
			params.allowRepost = false;
		}
		*/

		//println 'item->' + item.name + ' allow repost->' + params.allowRepost + '\n';
	}

	void dateProcess( params, item, header ) {
		//println 'header->' + header;
		//println 'values->' + params.values;

		//params.postingitem.allowpost = false;
		if (item.isincrementafterposting == true && params.values[header.name]) {
			if (!params.incrementAfterPosting[header.name]) params.incrementAfterPosting[header.name] = 0;
			params.incrementAfterPosting[header.name] += 1;
			params.postingitem.allowpost = true;
			/*
			def val = params.values[item.name];
			if (val && params.PAYMENT) {
				params.PAYMENT.current[item.name] = val;
			}
			*/
			//println 'name-> ' + item.name + ' val-> ' + val;

			//params.PAYMENT.addToCurrentSchedule += 1;
			println 'allow updatedb->' + item.allowupdatedb;
			if (item.allowupdatedb == true) {
				SERVICE.updateDB( item, params.values, params.incrementAfterPosting, params.dbParams );
			}
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