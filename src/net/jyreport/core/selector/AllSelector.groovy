package net.jyreport.core.selector

import net.jyreport.core.grid.DataGrid
import net.jyreport.core.*

/**
 *
 */
class AllSelector extends Selector {
	
	Closure condition
	
	AllSelector(){
	}
	
	AllSelector(Closure condition){
		this.condition=condition
	}
	
	List<DataGrid> select(){
		List<DataGrid> grids=reportData.allDataGrids
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

