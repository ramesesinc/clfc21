package ils_rules.general.actions;

import com.rameses.rules.common.*;
import com.rameses.util.*;

public class ValidateConstraintExpression implements RuleActionHandler {
	
	def SERVICE;

	public void execute(def params, def drools) {
		def allowpost = true;
		def list = [];
		if (params.constraints) {
			allowpost = false;
			try {
				list = SERVICE.buildConstraintExpressionList( params );
			} catch (Exception e) {
				e.printStackTrace();
			}

			for (def i = 0; i < list.size(); i++) {
				def val = list.get( i );
				if (val != null) {
					def flag = (new ActionExpression(val.expr, val.bindings)).booleanValue;
					//println 'expr->' + val.expr + ' bindings->' + val.bindings + ' flag->' + flag;
					allowpost = flag;
					if (flag == false) {
						break;
					}	
				}
			}
		}
		params.allowPostByConstraint = allowpost;
	}
}