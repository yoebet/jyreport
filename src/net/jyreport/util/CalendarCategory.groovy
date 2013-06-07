package net.jyreport.util

import static java.util.Calendar.*

@Category(Calendar)
class CalendarCategory {

	void goback(int field,int amount){
		add(field,-amount)
	}

	void setDayKeepMonth(day){
		if(day){
			int amd=getActualMaximum(DAY_OF_MONTH)
			if(day>amd){
				day=amd
			}
			set(DAY_OF_MONTH,day)
		}
	}

	int monthDiff(Calendar anotherCal){
		return (anotherCal[Calendar.YEAR]-this[Calendar.YEAR])*12 +
		anotherCal[MONTH]-this[MONTH]
	}

	void toNextDay(day){
		if(this[DAY_OF_MONTH] >= day){
			if(get(MONTH)<11){
				roll(MONTH,1)
			}else{
				add(MONTH,1)
			}
		}
		setDayKeepMonth(day)
	}

	void toLastDay(day){
		if(this[DAY_OF_MONTH] <= day){
			if(get(MONTH)>0){
				roll(MONTH,-1)
			}else{
				add(MONTH,-1)
			}
		}
		setDayKeepMonth(day)
	}

	void toNextQDay(day){
		toNextDay(day)
		int month=this[MONTH]+1
		if(month % 3 != 0){
			add(MONTH,3-(month % 3))
		}
	}

	void toLastQDay(day){
		toLastDay(day)
		int month=this[MONTH]+1
		if(month % 3 != 0){
			goback(MONTH,month%3)
		}
	}

	void toNextHDay(day){
		toNextDay(day)
		int month=this[MONTH]+1
		add(MONTH,6-(month % 6))
	}

	void toLastHDay(day){
		toLastDay(day)
		int month=this[MONTH]+1
		goback(MONTH,month%6)
	}
	
	
	void toLastMonthEnd(){
		add(MONTH,-1)
		int amd=getActualMaximum(DAY_OF_MONTH)
		set(DAY_OF_MONTH,amd)
	}

}
