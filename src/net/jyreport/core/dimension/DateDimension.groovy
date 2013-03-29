package net.jyreport.core.dimension

import net.jyreport.core.*

/**
 *
 */
class DateDimension extends Dimension {
	
	String name='date'
	
	String text='日期'
	
	Class modelClass=Date
	
	String datePattern='yyyy-M-d'
	
	String getName(model){
		model.format(getDatePattern())
	}
	
	def getValue(model){
		model
	}
}