package ils_rules.general.actions;

import com.rameses.rules.common.*;
import com.rameses.util.*;

public class BuildConstraintExpression implements RuleActionHandler {
	def SERVICE;
	public void execute(def params, def drools) {
		try {
			SERVICE.buildConstraintExpression(params);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}