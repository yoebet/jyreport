package net.jyreport.core.datatype

import java.text.*
import net.jyreport.core.*

/**
 *
 */
class IntegerType extends NumericType {
	
	String formatValue(def value,Display display){
		if(value==null){
			return null
		}
		def un=getUnit()
		if(un){
			switch(un){
				case Number:
				value/=un
				break
				case String:
				if(un.isNumber()){
					value/=un.toInteger()
				}
				break
				default:
				println "unknown unit: ${un}"
			}
		}
		if(formatter==null){
			formatter=new DecimalFormat(pattern ?: display?.integerPattern)
		}
		return formatter.format(value)
	}
}

