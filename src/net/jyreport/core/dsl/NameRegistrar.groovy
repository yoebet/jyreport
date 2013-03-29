package net.jyreport.core.dsl

import net.jyreport.core.datatable.*
import net.jyreport.core.dimension.*
import net.jyreport.core.datatype.*

/**
 *
 */
class NameRegistrar {
	
	protected Map<String,Class> predefinedDataTypes=[
		numeric: NumericType,
		currency: CurrencyType,
		integer: IntegerType,
		percent: PercentType,
		date: DateType,
		text: TextType
	]
	
	Map<String,Class> getDataTypeClasses(){
		predefinedDataTypes
	}
	
	Map<String,Class> getDimensionClasses(){
		[:]
	}
	
	Map<String,Class> getDataTableClasses(){
		[:]
	}
	
	
}

