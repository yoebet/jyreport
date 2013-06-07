package net.jyreport.core.grid

import net.jyreport.core.*


/**
 *
 */
class HeadGrid extends Grid {
	
	Head head
	
	HeadGrid higherHeadGrid
	
	List<HeadGrid> lowerHeadGrids
	
	List<DataGrid> grids
	
	boolean isRowHead
	
	List<Integer> rows
	
	List<Integer> cols
	
	Integer layer
	
	protected boolean paramsEvaluated
	
	def init(){
		//println "init ${this}"
		evalDataType()
		evalEvaluator()
		evalParams()
		evalRows()
		evalCols()
		evalLayer()
	}
	
	DataType evalDataType(){
		if(dataType==null){
			dataType=higherHeadGrid?.evalDataType()
		}
		return dataType
	}
	
	def evalEvaluator(){
		if(evaluator==null){
			evaluator=higherHeadGrid?.evalEvaluator()
		}
		return evaluator
	}
	
	Map<String,Object> evalParams(){
		if(!paramsEvaluated){
			if(params==null){
				params=[:]
			}
			if(higherHeadGrid!=null){
				params=higherHeadGrid.evalParams()+params
			}
			paramsEvaluated=true
		}
		return params
	}
	
	List<Integer> evalRows(){
		if(!isRowHead){
			return null
		}
		if(rows!=null){
			return rows
		}
		if(!lowerHeadGrids){
			rows=[row]
			return rows
		}
		rows=[]
 		lowerHeadGrids.each {
			rows.addAll(it.evalRows())
		}
		return rows
	}
	
	List<Integer> evalCols(){
		if(isRowHead){
			return null
		}
		if(cols!=null){
			return cols
		}
		if(!lowerHeadGrids){
			cols=[col]
			return cols
		}
		cols=[]
 		lowerHeadGrids.each {
			cols.addAll(it.evalCols())
		}
		return cols
		
	}
	
	Integer evalLayer(){
		if(parent==null){
			layer=0
		}else{
			layer=parent.evalLayer()+1
		}
		return layer
	}
	
	int compareTo(Object another){
		if(another==null){
			return 1
		}
		if(!another instanceof Grid){
			return -1
		}
		if(isRowHead){
			if(row==null || another.row==null){
				if(row==null && another.row==null){
					return 0
				}
				return (row==null)? -1 : 1
			}
			return row.compareTo(another.row)
		}else{
			if(col==null || another.col==null){
				if(col==null && another.col==null){
					return 0
				}
				return (col==null)? -1 : 1
			}
			return col.compareTo(another.col)
		}
	}
	
	String getText(){
		head.name
	}
	
	Dimension getDimension(){
		head.dimension
	}
	
	def getModel(){
		head.model
	}
	
	String toString(){
		text
	}
}

