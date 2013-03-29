package net.jyreport.core.datatype

import java.text.*
import net.jyreport.core.*

/**
 *
 */
class NumericType extends DataType {
	
	def unit
	
	NumberFormat formatter
	
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
			formatter=new DecimalFormat(pattern ?: display?.numericPattern)
		}
		return formatter.format(value)
	}
	
}

