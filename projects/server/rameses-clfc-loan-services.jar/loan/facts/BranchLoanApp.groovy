package loan.facts;

import java.util.*;
import org.joda.time.*;

public class BranchLoanApp {

	String appid;
	double balance;
	double intrate;
	double interest;
	String postingterm;
	Date lastscheduledate;
	int noofdaysfromlastschedule;
	int noofmonthsfromlastschedule;
	int noofyearsfromlastschedule;

	public BranchLoanApp( o ) {
		if (o.appid) appid = o.appid;
		if (o.balance) balance = o.balance;
		if (o.intrate) intrate = o.intrate;
		if (o.interest) interest = o.interest;
		if (o.postingterm) postingterm = o.postingterm;
		def start;
		if (o.lastscheduledate) {
			lastscheduledate = o.lastscheduledate;
			start = new LocalDate(lastscheduledate);
		} else {
			start = new LocalDate(parseDate(o.dtreleased));
		}

		if (o.currentdate) {
			def end = new LocalDate(parseDate(o.currentdate));

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
			balance 					: balance,
			intrate 					: intrate,
			interest					: interest,
			postingterm 				: postingterm,
			lastscheduledate			: lastscheduledate,
			noofdaysfromlastschedule	: noofdaysfromlastschedule,
			noofmonthsfromlastschedule 	: noofmonthsfromlastschedule,
			noofyearsfromlastschedule	: noofyearsfromlastschedule
		];

		return data;
	}
}