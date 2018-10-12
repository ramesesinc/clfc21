package ils_rules.general.actions;

import com.rameses.rules.common.*;
import com.rameses.util.*;

public class BuildAppBindings implements RuleActionHandler {

	def SERVICE;

	public void execute(def params, def drools) {
		def bindings = [:];

		try {
			bindings = SERVICE.buildAppBindings();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		params.bindings = bindings;
		/*
		try {
			SERVICE.buildConstraintExpression(params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		*/
	}
}