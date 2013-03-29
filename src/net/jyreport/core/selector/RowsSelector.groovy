package net.jyreport.core.selector

import net.jyreport.core.grid.DataGrid
import net.jyreport.core.*

/**
 *
 */
class RowsSelector extends Selector {
	
	def rows
	
	Closure headGridCondition
	
	Closure condition
	
	RowsSelector(Closure headGridCondition,Closure condition=null){
		this.headGridCondition=headGridCondition
		this.condition=condition
	}
	
	RowsSelector(rows,Closure headGridCondition=null,Closure condition=null){
		this.rows=rows
		this.headGridCondition=headGridCondition
		this.condition=condition
	}
	
	List<DataGrid> select(){
		
		List<DataGrid> grids=reportData.rowsGrid(rows,headGridCondition)
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

