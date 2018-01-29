package smc.facts;

import java.util.*;

public class SMCCondition
{
	String smcid;
	String conditionid;
	String stringvalue;
	double decimalvalue;
	Date datevalue;
	String computationterm;

	public SMCCondition( o, datatype, value ) {
		if (o.smcid) smcid = o.smcid;
		if (o.conditionid) conditionid = o.conditionid;
		if (o.computationterm) computationterm = o.computationterm;

		switch (datatype.toLowerCase()) {
			case 'decimal' 	: decimalvalue = Double.parseDouble(String.valueOf(value)); break;
			case 'date'		: datevalue = java.sql.Date.valueOf(String.valueOf(value)); break;
			default 		: stringvalue = String.valueOf(value); break;
		}
	}

	def toMap() {
		def data = [
			smcid 			: smcid,
			conditionid		: conditionid,
			stringvalue		: stringvalue,
			decimalvalue	: decimalvalue,
			datevalue		: datevalue,
			computationterm	: computationterm
		];
		return data;
	}
}
