package smc.facts;

import java.util.*;
import org.joda.time.*;

public class SMC {

	String appid;
	String smcid;
	String ledgerid;
	double balance
	String postingterm;
	Date lastscheduledate;
	int noofdaysfromlastschedule;
	int noofmonthsfromlastschedule;
	int noofyearsfromlastschedule;

	public SMC( o ) {
		if (o.appid) appid = o.appid;
		if (o.smcid) smcid = o.smcid;
		if (o.ledgerid) ledgerid = o.ledgerid;
		if (o.balance) balance = o.balance;
		if (o.postingterm) postingterm = o.postingterm;
		def start;
		if (o.lastscheduledate) {
			lastscheduledate = o.lastscheduledate;
			start = new LocalDate(lastscheduledate);
		} else if (o.dtstarted) {
			start = new LocalDate(parseDate(o.dtstarted));
		}

		if (o.currentdate) {
			end = new LocalDate(parseDate(o.currentdate));
			
			noofdaysfromlastschedule = Days.daysBetween(start, end).getDays();
			noofmonthsfromlastschedule = Months.monthsBetween(start, end).getMonths();
			noofyearsfromlastschedule = Years.yearsBetween(start, end).getYears();
		}
	}

	def parseDate( date ) {
		if (!date) return null;

		if (date instanceof Date) {
			return date;
		} else {
			return java.sql.Date.valueOf(date);
		}
	}

	def toMap() {
		def data = [
			appid 						: appid,
			smcid 						: smcid,
			ledgerid					: ledgerid,
			balance 					: balance,
			postingterm 				: postingterm,
			lastscheduledate			: lastscheduledate,
			noofdaysfromlastschedule 	: noofdaysfromlastschedule,
			noofmonthsfromlastschedule 	: noofmonthsfromlastschedule,
			noofyearsfromlastschedule	: noofyearsfromlastschedule
		]
		return data;
	}
}