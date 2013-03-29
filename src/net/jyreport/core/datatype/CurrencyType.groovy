package net.jyreport.core.datatype

import java.text.*
import net.jyreport.core.*

/**
 *
 */
class CurrencyType extends NumericType {
	
	String formatValue(def value,Display display){
		if(value==null){
			return null
		}
		def un=getUnit() ?: display?.currencyUnit
		if(un){
			switch(un){
				case '元':
				break
				case '万':
				case '万元':
				value/=10000.0d
				break
				case '亿':
				case '亿元':
				value/=100000000.0d
				break
				case Number:
				value/=un
				break
				case String:
				if(un.isNumber()){
					value/=un.toInteger()
					break
				}
				default:
				println "unknown unit: ${un}"
			}
		}
		if(formatter==null){
			formatter=new DecimalFormat(pattern ?: display?.currencyPattern)
		}
		return formatter.format(value)
	}
}

