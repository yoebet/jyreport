package net.jyreport.core.selector

import net.jyreport.core.grid.DataGrid
import net.jyreport.core.*

/**
 *
 */
class OrSelector extends CombinedSelector {
	
	OrSelector(selector1,selector2){
		super(selector1,selector2)
	}
	
	List<DataGrid> select(){
		def grids1=selector1.select()
		def grids2=selector2.select()
		(grids1-grids2)+grids2
	}
}

