package net.jyreport.core.headprocessor

import net.jyreport.util.*
import net.jyreport.core.*

/**
 *
 */
class DateSeriesProcessor extends AbstractHeadProcessor {
	
	List<Head> expand(){
		
		setDimensionAttributes()
		
		def baseDate
		def params=headCriteria?.params
		if(params!=null){
			baseDate=params[dimension.baseDateParamName]
		}
		if(baseDate==null){
			baseDate=new Date()
			baseDate.clearTime()
		}
		int maxDays=dimension.maxDays
		
		List<Date> models=[]
		
		use([DateCategory,CalendarCategory]){
			def currentDate=baseDate
			if(dimension.fromNextDay){
				currentDate++
			}
			maxDays.times{
				models << currentDate
				currentDate++
			}
		}
		
		return dimension.wrapHeads(models)
	}
	
}

