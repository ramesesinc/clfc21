package ils_rules.general.actions;

import com.rameses.rules.common.*;
import com.rameses.util.*;

public class BuildBindings implements RuleActionHandler {

	def SERVICE;

	public void execute(def params, def drools) {
		def fields = params.fields;
		def dbParams = params.dbParams;
		def bindings = SERVICE.buildBindings( fields, dbParams );
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