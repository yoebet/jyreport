package net.jyreport.core.dsl

import net.jyreport.core.grid.*
import net.jyreport.core.*

/**
 *
 */
class HeadBuilder extends BaseBuilder {
	
	Head head
	
	HeadBuilder(head){
		this.head=head
		setCurrent(head)
	}

    protected void setClosureDelegate(Closure closure, Object node) {
		closure.resolveStrategy=Closure.DELEGATE_FIRST
		if(node instanceof Grid){
			def gb=new GridBuilder(node)
			gb.nameRegistrar=this.nameRegistrar
			closure.setDelegate(gb)
		}else{
			closure.setDelegate(this)
		}
    }
	
	void formula(Closure f){
		if(current==head){
			head.setFormula(f)
		}else{
			logUnknownName('formula',f)
		}
	}
	
	void initializedCallback(Closure f){
		if(current==head){
			head.setInitializedCallback(f)
		}else{
			logUnknownName('initializedCallback',f)
		}
	}
	
	void evaluatedCallback(Closure f){
		if(current==head){
			head.setEvaluatedCallback(f)
		}else{
			logUnknownName('evaluatedCallback',f)
		}
	}
	
	void aggregator(Closure f){
		if(current==head){
			head.setAggregator(f)
		}else{
			logUnknownName('aggregator',f)
		}
	}
	
    protected Object doCreateNode(Object name, Map attributes, Object value){
		def tGrid=head.templateGrid
		if(name=='grid' || name=='templateGrid'){
			if(value==null){
				tGrid=new HeadGrid()
			}else if(value instanceof HeadGrid){
				tGrid=value
			}else if(tGrid instanceof Class){
				tGrid=value.newInstance()
			}else{
				logUnknownValue(name,value)
			}
			if(tGrid==null){
				tGrid=new HeadGrid()
			}
			if(attributes!=null){
				setProperties(tGrid,attributes)
			}
			head.templateGrid=tGrid
			return tGrid
		}
		if(name=='dataType'){
			if(tGrid==null){
				tGrid=head.templateGrid=new HeadGrid()
			}
			tGrid.dataType=parseDataType(value,attributes)
			return name
		}
		if(name=='model' && value==null && attributes!=null){
			head.model=attributes
		}else{
			setProperty(head,name,value,attributes)
		}
		name
    }
}
