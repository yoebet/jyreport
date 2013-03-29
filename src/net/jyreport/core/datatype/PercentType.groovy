package net.jyreport.core.datatype

import java.text.*
import net.jyreport.core.*

/**
 *
 */
class PercentType extends DataType {
	
	NumberFormat formatter
	
	String formatValue(def value,Display display){
		if(value==null){
			return null
		}
		def pat=pattern ?: display?.percentPattern
		if(formatter==null){
			formatter=new DecimalFormat(pat)
		}
		if(pat.endsWith('%')){
			return formatter.format(value)
		}else{
			return formatter.format(value*100.0)
		}
	}
}

