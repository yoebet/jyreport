package net.jyreport.core.selector

import net.jyreport.core.grid.DataGrid
import net.jyreport.core.*

/**
 *
 */
class FixedSelector extends Selector {
	
	List<DataGrid> grids
	
	FixedSelector(grids){
		this.grids=grids
	}
	
	List<DataGrid> select(){
		grids
	}
}
