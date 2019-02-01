package loan.actions;

import com.rameses.rules.common.*;
import com.rameses.util.*;
import loan.facts.*;
import org.joda.time.*;

public class AddSamplePostingItem implements RuleActionHandler {
	
	def facts;
	def calendarSvc;
	
	public void execute(def params, def drools) {

		def APP = params.LOANAPP;

		def ITEM = new SamplePostingItem();
		def counter = APP.counter;

		counter += 1;

		def schedule = DateUtil.add( APP.dtreleased, counter + "d");
		if (schedule.compareTo(APP.dtbill) <= 0) {
			APP.counter = counter;
			ITEM.dtschedule = schedule;
			facts << ITEM;
			drools.update(APP);
		}

		/*
		def app = params.LOANAPP;
		int c = app.counter;
		def b = new LoanPostingItem();
		def p = params.PAYMENT;
		//println 'counter ' + c;
		//b.amtpaid = params.amtpaid;

		//println 'app last count ' + app.lastcounter;
		app.counter += 1;
		b.day = app.counter;
		
		if (app.counter > app.lastcounter) facts << b;
		*/
		//drools.update( app );
	}

}