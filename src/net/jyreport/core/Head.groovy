package net.jyreport.core

import net.jyreport.core.grid.*
import static net.jyreport.core.grid.DataGrid.*

/**
 *
 */
class Head implements Serializable,Cloneable {

	Dimension dimension

	def model

	String name

	def value

	boolean derived

	boolean aggregate

	Head parent

	List<Head> children
	
	boolean carryParentHeadParam

	Integer position

	HeadGrid templateGrid

	def getValue(){
		value ?: dimension.getValue(model)
	}

	void addChild(Head child){
		if(children==null){
			children=[]
		}
		children << child
		child.parent=this
	}

	void setFormula(formula){
		if(templateGrid==null){
			templateGrid=new HeadGrid(head:this)
		}
		templateGrid.setFormula(formula)
	}

	void setInitializedCallback(initializedCallback){
		if(templateGrid==null){
			templateGrid=new HeadGrid(head:this)
		}
		templateGrid.setInitializedCallback(initializedCallback)
	}

	void setEvaluatedCallback(evaluatedCallback){
		if(templateGrid==null){
			templateGrid=new HeadGrid(head:this)
		}
		templateGrid.setEvaluatedCallback(evaluatedCallback)
	}

	void setAggregator(aggregator){
		if(templateGrid==null){
			templateGrid=new HeadGrid(head:this)
		}
		templateGrid.setAggregator(aggregator)
	}

	void setDataType(dataType){
		if(templateGrid==null){
			templateGrid=new HeadGrid(head:this)
		}
		templateGrid.dataType=dataType
	}

	String toString(){
		name
	}
	
	Object clone(){
		
		def head2=super.clone()
		head2.children=children?.collect{it.clone()}
		head2.templateGrid=templateGrid?.clone()
		head2
	}

	static Head SUM_HEAD(){
		new Head(name:'合计',aggregate:true,formula:SUM)
	}

	static Head SUM_DERIVED_HEAD(){
		new Head(name:'合计',aggregate:true,formula:SUM_DERIVED)
	}

	static Head SUM_ALL_HEAD(){
		new Head(name:'合计',aggregate:true,formula:SUM_ALL)
	}

	static Head SUB_SUM_HEAD(){
		new Head(name:'小计',aggregate:true,formula:SUM)
	}
}

