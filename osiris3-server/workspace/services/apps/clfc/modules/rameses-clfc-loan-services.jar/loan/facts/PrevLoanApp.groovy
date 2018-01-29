package loan.facts;

import java.util.*;

public class PrevLoanApp {

	String appid;
	double amount;

	public PrevLoanApp( o ) {
		appid = o.appid;
		amount = o.amount;
	}

	def toMap() {
		return [
			appid 	: appid,
			amount 	: amount
		];
	}
}