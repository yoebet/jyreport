package net.jyreport.core.dimension

import net.jyreport.core.*
import net.jyreport.core.headprocessor.MonthEndProcessor

/**
 *
 */
class MonthEndDimension extends DateDimension {
	
	String name='monthEnd'
	
	def headProcessor=MonthEndProcessor
	
	String appendInParam='no'
	
	String appendBetweenParam='yes'
	
	int maxMonths=60
	
	String baseDateParamName='date'
	
	// 按月：null/M；Q：按季；按半年：H；按年：Y
	String overview
	
	// 对齐到季末、半年末、年末（overview为Q/H/Y时有效）
	boolean align
	
	//从下月末开始
	boolean fromNextMonth
	
}