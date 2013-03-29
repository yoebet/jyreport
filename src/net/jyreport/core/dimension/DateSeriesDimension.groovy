package net.jyreport.core.dimension

import net.jyreport.core.*

/**
 *
 */
class DateSeriesDimension extends DateDimension {
	
	String name='dateSeries'
	
	int maxDays=60
	
	String baseDateParamName='date'
	
	boolean fromNextDay
}