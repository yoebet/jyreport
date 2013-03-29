package net.jyreport.core.selector

import net.jyreport.core.grid.DataGrid
import net.jyreport.core.*

/**
 *
 */
class ColumnsSelector extends Selector {
	
	def cols
	
	Closure headGridCondition
	
	Closure condition
	
	ColumnsSelector(Closure headGridCondition,Closure condition=null){
		this.headGridCondition=headGridCondition
		this.condition=condition
	}
	
	ColumnsSelector(cols,Closure headGridCondition=null,Closure condition=null){
		this.cols=cols
		this.headGridCondition=headGridCondition
		this.condition=condition
	}
	
	List<DataGrid> select(){
		
		List<DataGrid> grids=reportData.colsGrid(cols,headGridCondition)
		if(condition==null){
			return grids
		}
		condition.resolveStrategy=Closure.DELEGATE_FIRST
		return grids.grep{
			condition.delegate=it
			condition.call()
		}
	}
}

