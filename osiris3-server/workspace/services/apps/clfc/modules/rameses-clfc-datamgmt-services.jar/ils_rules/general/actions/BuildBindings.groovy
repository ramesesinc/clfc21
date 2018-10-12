package ils_rules.general.actions;

import com.rameses.rules.common.*;
import com.rameses.util.*;

public class BuildBindings implements RuleActionHandler {

	def SERVICE;

	public void execute(def params, def drools) {
		def fields = params.fields;
		def dbParams = params.dbParams;
		def bindings = [:];

		try {
			bindings = SERVICE.buildBindings( fields, dbParams );
		} catch (Exception e) {
			e.printStackTrace();
		}
		//println 'bindings->' + bindings;
		if (params.bindings == null) params.bindings = [:];
		params.bindings.putAll( bindings );
		/*
		try {
			SERVICE.buildConstraintExpression(params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		*/
	}
}