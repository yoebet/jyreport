package net.jyreport.core.datatype

import net.jyreport.core.*

/**
 *
 */
class DateType extends DataType {
	
	String formatValue(def value,Display display){
		if(value==null){
			return null
		}
		value.format(pattern ?: display?.datePattern)
	}
}

