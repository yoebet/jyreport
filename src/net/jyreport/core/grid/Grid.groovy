package net.jyreport.core.grid

import net.jyreport.core.*
import net.jyreport.core.support.*

/**
 *
 */
class Grid extends Formula {
	
	Integer id
	
	DataType dataType
	
	boolean derived
	
	Integer row
	
	Integer col
	
	Map<String,Object> params
	
	Grid parent
	
	List<Grid> children
	
	ReportData reportData
	
	Closure initializedCallback
	
	Closure evaluatedCallback
	
	//选择出多条数据时应用的聚集器，如：{list->list.sum{it.value}}
	Closure aggregator
	
	//convey to view
	Map<String,Object> properties
	
	def getContext(){
		reportData.context
	}
	
	def getRuntimeContext(){
		reportData.runtimeContext
	}
	
	def getRc(){
		reportData.runtimeContext
	}
	
	def getReport(){
		reportData.report
	}
	
	def getDataMap(){
		def gridsDataKey='gridsData'
		def gridsDataMap=reportData.runtimeContext[gridsDataKey]
		if(gridsDataMap==null){
			gridsDataMap=[:].withDefault{[:]}
			reportData.runtimeContext[gridsDataKey]=gridsDataMap
		}
		gridsDataMap[this]
	}
	
	def data(String key){
		dataMap[key]
	}
	
	def data(String key,def value){
		dataMap[key]=value
	}
	
	def addChild(Grid child){
		if(child==null){
			return
		}
		if(children==null){
			children=[]
		}
		children << child
		child.parent=this
	}
	
	void setInitializedCallback(Closure icb){
		if(icb){
			initializedCallback=icb.clone()
			initializedCallback.delegate=this
			initializedCallback.resolveStrategy=Closure.DELEGATE_FIRST
		}
	}
	
	void setEvaluatedCallback(Closure ecb){
		if(ecb){
			evaluatedCallback=ecb.clone()
			evaluatedCallback.delegate=this
			evaluatedCallback.resolveStrategy=Closure.DELEGATE_FIRST
		}
	}
	
	void setAggregator(Closure agg){
		if(agg){
			aggregator=agg.clone()
			aggregator.delegate=this
			aggregator.resolveStrategy=Closure.DELEGATE_FIRST
		}
	}
	
	int compareTo(Object another){
		if(usingCompareOperator){
			return super.compareTo(another)
		}
		if(another==null){
			return 1
		}
		if(!(another instanceof Grid)){
			return -1
		}
		if(row != another.row){
			if(row==null && another.row!=null){
				return -1
			}
			if(row!=null && another.row==null){
				return 1
			}
			return row-another.row
		}
		
		if(col==another.col){
			return 0
		}
		if(col==null && another.col!=null){
			return -1
		}
		if(col!=null && another.col==null){
			return 1
		}
		return col-another.col
	}
	
	boolean equals(Object another){
		if(usingCompareOperator){
			return super.equals(another)
		}
		this.is(another)
	}
	
}

