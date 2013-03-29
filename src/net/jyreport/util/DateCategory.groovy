package net.jyreport.util

import static java.util.Calendar.*

@Category(Date)
class DateCategory {

	
    Date add(int field,int amount){
		change {cal->
			cal.add(field,amount)
		}
    }
	
    Calendar toCalendar(){
        def cal=Calendar.instance
        cal.time=this
        return cal
    }

    Date change(calCallback){
        def cal=Calendar.instance
        cal.time=this
        calCallback(cal)
        return cal.time
    }

    int dayOfMonth(){
        return toCalendar()[DAY_OF_MONTH]
    }

    int currentMonth(){
        return toCalendar()[MONTH]+1
    }

    def withCalendar(calCallback){
        return calCallback(toCalendar())
    }

    def withAnotherDate(Date anotherDate,calCallback){
        return calCallback(toCalendar(),anotherDate.toCalendar())
    }
	
	boolean isSameMonth(Date anotherDate){
		return withAnotherDate(anotherDate,{cal1,cal2->
				cal1[Calendar.YEAR]==cal2[Calendar.YEAR] && 
				cal1[Calendar.MONTH]==cal2[Calendar.MONTH]
			})
	}
	
	boolean isMonthEnd(){
		def cal=toCalendar()
		cal[DAY_OF_MONTH]==cal.getActualMaximum(DAY_OF_MONTH)
	}

    Date rollMonths(months,day=null){//keep day
        return change { cal->
			day=day ?: cal[DAY_OF_MONTH]
            cal[DAY_OF_MONTH]=11
            cal.add(MONTH,months)
            cal.setDayKeepMonth(day)
        }
    }

    Date nextMonth(keepMonthEnd=false){//keep day
        return change { cal->
			boolean isMonthEnd
			if(keepMonthEnd){
				int d=cal[DAY_OF_MONTH]
				int amd=cal.getActualMaximum(DAY_OF_MONTH)
				isMonthEnd=d==amd
			}
            if(cal[MONTH]<11){
                cal.roll(MONTH,1)
            }else{
                cal.add(MONTH,1)
            }
			if(keepMonthEnd && isMonthEnd){
				cal[DAY_OF_MONTH]=cal.getActualMaximum(DAY_OF_MONTH)
			}
        }
    }
	
	Date toMonthEnd(){
		change {
			it[DAY_OF_MONTH]=it.getActualMaximum(DAY_OF_MONTH)
		}
	}

    Date nextMonthEnd(){
        return change { cal->
            cal.add(MONTH,1)
			cal[DAY_OF_MONTH]=cal.getActualMaximum(DAY_OF_MONTH)
        }
	}

    Date nextNMonthEnd(n){
        return change { cal->
            cal.add(MONTH,n)
			cal[DAY_OF_MONTH]=cal.getActualMaximum(DAY_OF_MONTH)
        }
	}

    Date lastMonth(keepMonthEnd=false){//keep day
        return change { cal->
			boolean isMonthEnd
			if(keepMonthEnd){
				int d=cal[DAY_OF_MONTH]
				int amd=cal.getActualMaximum(DAY_OF_MONTH)
				isMonthEnd=d==amd
			}
			if(cal[MONTH]>1){
				cal.roll(MONTH,-1)
			}else{
				cal.add(MONTH,-1)
			}
			if(keepMonthEnd && isMonthEnd){
				cal[DAY_OF_MONTH]=cal.getActualMaximum(DAY_OF_MONTH)
			}
        }
    }

    //转到下一m月d日，如不存在，则为m月最后一日
    Date toNextMD(m,d=null){
        def cal=toCalendar()
        int td=cal[DAY_OF_MONTH]
        cal[DAY_OF_MONTH]=1
        int months=m-(cal[MONTH]+1)
        cal.roll(MONTH,months)
        if(!d){
            d=td
        }
		int amd=cal.getActualMaximum(DAY_OF_MONTH)
		if(d>amd){
			d=amd
		}
        if(months<0 || (months==0 && td>=d)){
            cal[Calendar.YEAR]++
        }
		cal.setDayKeepMonth(d)

        return cal.time
    }

    //转到上一m月d日，如不存在，则为m月最后一日
    Date toLastMD(m,d=null){
        def cal=toCalendar()
        int td=cal[DAY_OF_MONTH]
        cal[DAY_OF_MONTH]=1
        int months=m-(cal[MONTH]+1)
        cal.roll(MONTH,months)
        if(!d){
            d=td
        }
		int amd=cal.getActualMaximum(DAY_OF_MONTH)
		if(d>amd){
			d=amd
		}
        if(months>0 || (months==0 && td<=d)){
            cal[Calendar.YEAR]--
        }
		cal.setDayKeepMonth(d)

        return cal.time
    }

    int monthDiff(Date anotherDate){
        withAnotherDate(anotherDate,{cal1,cal2-> cal1.monthDiff(cal2)})
    }

    int[] ymddDiff(Date anotherDate){
        def callback={cal1,cal2->
            int[] ymdd=[0,0,0,0]
            int months=cal1.monthDiff(cal2)
            if(cal1[DAY_OF_MONTH]>cal2[DAY_OF_MONTH]){
                months--
            }
            ymdd[0]=months/12
            ymdd[1]=months%12
            cal1[Calendar.YEAR]+=ymdd[0]
            ymdd[2]=cal2-cal1
            cal1[MONTH]+=ymdd[1]
            ymdd[3]=cal2-cal1
            return ymdd
        }
        withAnotherDate(anotherDate,callback)
    }
	

    Date toLastDay(day){
        change {cal-> cal.toLastDay(day)}
    }

    Date toLastQDay(day){
        change {cal-> cal.toLastQDay(day)}
    }

    Date toLastHDay(day){
        change {cal-> cal.toLastHDay(day)}
    }
	
    Date lastMonthEnd(){
        change {cal-> cal.toLastMonthEnd()}
	}

	//上旬末
    Date toLastTdEnd(){
        change {cal->
			int day=cal[DAY_OF_MONTH]
			if(day>20){
				cal[DAY_OF_MONTH]=20
			}else if(day>10){
				cal[DAY_OF_MONTH]=10
			}else{
				cal.toLastMonthEnd()
			}
		}
    }

    Date toLastQEnd(){
        toLastQDay(31)
    }

    Date toLastHEnd(){
        toLastHDay(31)
    }

    Date toLastYearEnd(){
        change {cal->
			cal[Calendar.YEAR]--
            cal[MONTH]=11
			cal[DAY_OF_MONTH]=31
		}
    }

    Date toNextDay(day){
        change {cal-> cal.toNextDay(day)}
    }

    Date toNextQDay(day){
        change {cal-> cal.toNextQDay(day)}
    }

    Date toNextHDay(day){
        change {cal-> cal.toNextHDay(day)}
    }

}
