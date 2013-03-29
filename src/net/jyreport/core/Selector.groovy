package net.jyreport.core

import net.jyreport.core.grid.DataGrid
import net.jyreport.core.selector.*

/**
 *
 */
abstract class Selector {
	
	ReportData reportData
	
	abstract List<DataGrid> select()
	
	protected Selector toSelector(any){
		if(any instanceof Selector){
			return any
		}
		def bs=new BasicSelector(any)
		bs.reportData=reportData
		bs
	}
	
	Selector and(another){
		new AndSelector(this,another)
	}
	
	Selector or(another){
		new OrSelector(this,another)
	}
	
	Selector not(another){
		new NotSelector(this,another)
	}
	
	Selector reverse(){
		new NotSelector(this)
	}
	
	static Selector buildSelector(location){
		if(location instanceof Selector){
			return location
		}
		new BasicSelector(location)
	}
	
	static Selector buildSelector(location, Closure condition){
		new BasicSelector(location,condition)
	}
	
	static Selector sel(location, Closure condition=null){
		buildSelector(location,condition)
	}
	
	static Selector selectAll(Closure condition=null){
		new AllSelector(condition)
	}
	
	static Selector selectRows(int... rows){
		new RowsSelector(rows)
	}
	
	static Selector selectRows(rows, Closure headGridCondition=null, Closure condition=null){
		new RowsSelector(rows,headGridCondition,condition)
	}
	
	static Selector selectRows(Closure headGridCondition, Closure condition=null){
		new RowsSelector(headGridCondition,condition)
	}
	
	static Selector selectColumns(int... cols){
		new ColumnsSelector(cols)
	}
	
	static Selector selectColumns(cols, Closure headGridCondition=null, Closure condition=null){
		new ColumnsSelector(cols,headGridCondition,condition)
	}
	
	static Selector selectColumns(Closure headGridCondition, Closure condition=null){
		new ColumnsSelector(headGridCondition,condition)
	}
	
}
