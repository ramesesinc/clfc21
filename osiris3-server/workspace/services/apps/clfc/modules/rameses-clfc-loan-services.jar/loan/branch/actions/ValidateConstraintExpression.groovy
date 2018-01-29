package loan.branch.actions;

import com.rameses.rules.common.*;
import com.rameses.util.*;

public class ValidateConstraintExpression implements RuleActionHandler {
	
	public void execute(def params, def drools) {
		def action = params.action;
		params.value = action.booleanValue;
	}
}