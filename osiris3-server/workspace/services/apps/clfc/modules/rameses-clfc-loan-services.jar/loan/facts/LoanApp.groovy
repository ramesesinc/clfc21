package loan.facts;

import java.util.*;

public class LoanApp {
	
	String appid;
	double principal;
	double interest;
	double absentrate;
	double absentpenalty;
	double underpytrate;
	double intrate;		
	double schedule;
	int term;
	Date startdate;
	Date scheduledate;
	int counter;
	int lastcounter;
	int noofdays;
	String paymentschedule;
	String loantype;
	String apptype
	double netamount;
	double totalcharges;
	int loancount;
	List charges;
	String prevloanid;
	Date dtreleased
	Date dtmatured;

	public LoanApp(o) {
		//if (!o.appid) throw new Exception("Error in creating Loan App Fact. appid is required.");
		
		if (o.appid) appid = o.appid;
		if (o.principal) principal = o.principal;
		if (o.intrate) intrate = o.intrate;
		if (o.absentrate) absentrate = o.absentrate;
		if (o.absentpenalty) absentpenalty = o.absentpenalty;
		if (o.interest) interest = o.interest;
		if (o.underpytrate) underpytrate = o.underpytrate;
		if (o.startdate) startdate = parseDate(o.startdate);
		if (o.scheduledate) scheduledate = o.scheduledate;
		if (o.schedule) schedule = o.schedule;
		if (o.term) term = o.term;
		if (o.paymentschedule) paymentschedule = o.paymentschedule;
		if (o.loantype) loantype = o.loantype;
		if (o.apptype) apptype = o.apptype;
		if (o.loancount) loancount = o.loancount;
		if (o.prevloanid) prevloanid = o.prevloanid;
		if (o.dtreleased) dtreleased = o.dtreleased;
		if (o.dtmatured) dtmatured = o.dtmatured;
		charges = [];

		counter = 0;
		noofdays = 0;
		lastcounter = 0;
	}

	def parseDate( date ) {
		if (date == null) return null;
		if (date instanceof Date) {
			return date;
		} else {
			return java.sql.Date.valueOf(date);
		}
	}

	def toMap() {
		return [
			appid			: appid,
			principal		: principal,
			interest		: interest,
			absentrate		: absentrate,
			absentpenalty	: absentpenalty,
			underpytrate 	: underpytrate,
			intrate			: intrate,
			schedule 		: schedule,
			term 			: term,
			startdate 		: startdate,
			scheduledate 	: scheduledate,
			counter			: counter,
			noofdays 		: noofdays,
			paymentschedule	: paymentschedule,
			loantype 		: loantype,
			apptype 		: apptype,
			netamount 		: netamount,
			totalcharges 	: totalcharges,
			loancount 		: loancount,
			prevloanid 		: prevloanid,
			dtreleased 		: dtreleased,
			dtmatured 		: dtmatured,
			charges 		: charges
		];
	}

}