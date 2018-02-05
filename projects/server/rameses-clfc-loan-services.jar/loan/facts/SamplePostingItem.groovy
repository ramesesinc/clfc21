package loan.facts;

public class SamplePostingItem
{
	Date dtschedule;
	double interest;
	double absentpenalty;
	double underpaymentpenalty;

	public SamplePostingItem() {}

	public SamplePostingItem( params ) {
		if (params.dtschedule) dtschedule = parseDate(params.dtschedule);
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
			dtschedule 			: dtschedule,
			interest 			: interest,
			absentpenalty 		: absentpenalty,
			underpaymentpenalty : underpaymentpenalty
		];
	}
}