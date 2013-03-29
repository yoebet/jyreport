package net.jyreport.core.grid

import net.jyreport.util.*
import net.jyreport.core.*

/**
 *
 */
@Category(DataGrid)
class FormulaCategory {
	
	def sum(String dim, values, iparams=null){
        if(iparams==null){
            values.sum{v([(dim):it])}
        }else{
            values.sum{v(iparams+[(dim):it])}
        }
	}
	
	def sumWithMinus(String dim, values, minusValues, iparams=null){
        if(iparams==null){
		    sum(dim,values) - sum(dim,minusValues)
        }else{
            values.sum{v(iparams+[(dim):it])} - minusValues.sum{v(iparams+[(dim):it])}
        }
	}
	
	def sum0(String dim, values, iparams){
        if(iparams==null){
            values.sum{v0([(dim):it])}
        }else{
            values.sum{v0(iparams+[(dim):it])}
        }
	}
	
	def sumWithMinus0(String dim, values, minusValues, iparams){
        if(iparams==null){
		    sum0(dim,values) - sum0(dim,minusValues)
        }else{
            values.sum{v0(iparams+[(dim):it])} - minusValues.sum{v0(iparams+[(dim):it])}
        }
	}

	def sumChildren(){
		children?.grep{!it.derived}?.sum() ?: 0.0
	}

    def sumLeftAll(){
        (0..<col).sum{colAbs(it)}
    }

    def sumHigherAll(){
        (0..<row).sum{rowAbs(it)}
    }
	
	//取上期末日期
	Date lastTermDate(frequency,date){
		
		use([DateCategory,CalendarCategory]){
			switch(frequency){
				case 'D':
				date-1
				break
				case 'W':
				date-7//TODO:
				break
				case 'T':
				date.toLastTdEnd()
				break
				case 'M':
				date.lastMonthEnd()
				break
				case 'Q':
				date.toLastQEnd()
				break
				case 'S':
				date.toLastHEnd()
				break
				case 'Y':
				date.toLastYearEnd()
				break
				default:
				date
			}
		}
	}
	
	def lastTerm(frequency=null,date=null){
		Report report=reportData.report
		if(frequency==null){
			frequency=report.frequency
		}
		if(frequency==null){
			frequency='M'
		}
		if(!['D','W','T','M','Q','S','Y'].contains(frequency)){
			throw new RuntimeException('错误的报表频度')
		}
		
		date=date ?: params['date']
		if(date==null){
			DataRequest defaultDataRequest=report.defaultDataRequest
			if(defaultDataRequest.params!=null){
				date=defaultDataRequest.params['date']
			}
		}
		if(date==null){
			throw new RuntimeException('未取到日期参数')
		}
		if(date instanceof String){
			//TODO:...
		}
		
		Date ltd=lastTermDate(frequency,date)
		
		return v([date:ltd])
	}
	
	//compare to last term(percent)
	def increaseRate(){
		def lt=lastTerm()
		if(lt){
			(v()-lt)/lt
		}else{
			0.0
		}
	}
}

