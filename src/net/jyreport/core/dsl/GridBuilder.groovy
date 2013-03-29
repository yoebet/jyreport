package net.jyreport.core.dsl

import net.jyreport.core.grid.*
import net.jyreport.core.*

/**
 *
 */
class GridBuilder extends BaseBuilder {
	
	def grid
	
	GridBuilder(grid){
		this.grid=grid
		setCurrent(grid)
	}
	
	void formula(Closure f){
		if(current==grid){
			current.setFormula(f)
		}else{
			logUnknownName('formula',f)
		}
	}
	
	void initializedCallback(Closure f){
		if(current==grid){
			current.setInitializedCallback(f)
		}else{
			logUnknownName('initializedCallback',f)
		}
	}
	
	void evaluatedCallback(Closure f){
		if(current==grid){
			current.setEvaluatedCallback(f)
		}else{
			logUnknownName('evaluatedCallback',f)
		}
	}
	
	void aggregator(Closure f){
		if(current==grid){
			current.setAggregator(f)
		}else{
			logUnknownName('aggregator',f)
		}
	}
	
	void value(Closure f){
		formula(f)
	}
	
    protected Object doCreateNode(Object name, Map attributes, Object value){
		if(current=='params'){
			grid.params[name]=value
			return name
		}
		if(current=='properties'){
			grid.properties[name]=value
			return name
		}
		if(current=='dataType'){
			setProperty(grid.dataType,name,value,attributes)
			return name
		}
		if(name=='dataType'){
			grid.dataType=parseDataType(value,attributes)
			return name
		}
		if(name=='params'){
			grid.params=(attributes!=null)? attributes : [:]
			return name
		}
		if(name=='properties'){
			grid.properties=(attributes!=null)? attributes : [:]
			return name
		}
		setProperty(grid,name,value,attributes)
		name
    }
}
