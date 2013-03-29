package net.jyreport.core.headprocessor

import net.jyreport.util.*
import net.jyreport.core.*

/**
 *
 */
class MonthEndProcessor extends AbstractHeadProcessor {
	
	List<Head> expand(){
		
		setDimensionAttributes()
		
		def baseDate
		def params=headCriteria?.params
		if(params!=null){
			baseDate=params[dimension.baseDateParamName]
		}
		if(baseDate==null){
			baseDate=new Date()
		}
		int maxMonths=dimension.maxMonths
		String overview=dimension.overview
		
		def pagination=headCriteria?.pagination
		
		List<Date> models=[]
		
		use([DateCategory,CalendarCategory]){
			Date firstMonth
			if(dimension.fromNextMonth){
				firstMonth=baseDate.nextMonthEnd()
			}else{
				firstMonth=baseDate.toMonthEnd()
			}
			Date uptoMonth=firstMonth.nextNMonthEnd(maxMonths-1)
			Date currentMonth
			
			int rollUnit=1
			if(overview){
				rollUnit=[Q:3,H:6,Y:12][overview] ?: rollUnit
				if(dimension.align && rollUnit>1){
					def cm=firstMonth.currentMonth()
					def roll=rollUnit-cm%rollUnit
					if(roll!=rollUnit){
						firstMonth=firstMonth.nextNMonthEnd(roll)
					}
				}
			}
			if(pagination && pagination.offset>1){
				firstMonth=firstMonth.nextNMonthEnd((pagination.offset-1)*rollUnit)
			}
			models << firstMonth
			
			currentMonth=firstMonth
			int monthsLeft=maxMonths-1
			if(pagination){
				monthsLeft=pagination.perPage-1
			}
			while(monthsLeft > 0){
				currentMonth=currentMonth.nextNMonthEnd(rollUnit)
				if(currentMonth > uptoMonth){
					break
				}
				models << currentMonth
				monthsLeft--
			}
			
			if(pagination){
				pagination.setTotal((int)(maxMonths/rollUnit))
			}
		}
		
		return dimension.wrapHeads(models)
	}
	
}

