package loan.facts;

import java.util.*;

class SampleLoanApp
{
	String appid;
	Date dtreleased;
	Date dtbill;
	double interest;
	double absentpenalty;
	double dailydue;
	int counter;

	public SampleLoanApp() {}

	public SampleLoanApp( params ) {
		if (params.appid) appid = params.appid;
		if (params.dtreleased) dtreleased = parseDate(params.dtreleased);
		if (params.dtbill) dtbill = parseDate(params.dtbill);
		if (params.interest) interest = params.interest;
		if (params.absentpenalty) absentpenalty = params.absentpenalty;
		if (params.dailydue) dailydue = params.dailydue;
		counter = 0
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
			dtreleased		: dtreleased,
			dtbill 			: dtbill,
			interest 		: interest,
			absentpenalty 	: absentpenalty,
			dailydue 		: dailydue
		];
	}

}