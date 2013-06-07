package net.jyreport.core.dsl

import net.jyreport.core.*

/**
 *
 */
class DimensionBuilder extends BaseBuilder {
	
	Dimension dimension
	
	protected headModel
	
	DimensionBuilder(dimension){
		this.dimension=dimension
		setCurrent(dimension)
	}

	protected void setClosureDelegate(Closure closure, Object node) {
		def del
		if(node instanceof Head){
			del=new HeadBuilder(node)
		}else if(node instanceof Dimension){
			del=new DimensionBuilder(node)
		}else if(node instanceof NameStrategy){
			del=new NameStrategyBuilder(node)
		}else{
			del=this
		}
		del.nameRegistrar=this.nameRegistrar
		closure.resolveStrategy=Closure.DELEGATE_FIRST
		closure.setDelegate(del)
	}
	
	void paramsCallback(Closure callback){
		if(current==dimension){
			current.paramsCallback=callback
		}
	}
	
	void dataRequestCallback(Closure callback){
		if(current==dimension){
			current.dataRequestCallback=callback
		}
	}
	
	void appendSubHeadCallback(Closure callback){
		if(current==dimension){
			current.appendSubHeadCallback=callback
		}
	}
	
	void subDimensionExpandParams(Closure callback){
		if(current==dimension){
			current.subDimensionExpandParams=callback
		}
	}
	
	void appendLowerHeadCallback(Closure callback){
		if(current==dimension){
			current.appendLowerHeadCallback=callback
		}
	}
	
	void lowerDimensionExpandParams(Closure callback){
		if(current==dimension){
			current.lowerDimensionExpandParams=callback
		}
	}
	
	void modelSorter(Closure callback){
		if(current==dimension){
			current.modelSorter=callback
		}
	}
	
	
	protected Object doCreateNode(Object name, Map attributes, Object value){
		def node=name
		if(current=='staticHeads'){
			if(name!='head'){
				logUnknownName(name,value)
				return null
			}
			def head
			if(value==null){
				head=new Head()
			}else if(value instanceof Head){
				head=value
			}else if(value instanceof Class){
				head=value.newInstance()
			}else{
				logUnknownValue(name,value)
				return null
			}
			if(attributes!=null){
				setProperties(head,attributes)
			}
			dimension.staticHeads << head
			return head
		}
		if(current=='headModels'){
			if(name!='model'){
				logUnknownName(name,value)
				return node
			}
			def headModel
			if(value==null){
				headModel=(attributes!=null)? attributes: [:]
			}else if(value instanceof Head){
				headModel=value
			}else if(value instanceof Class){
				headModel=value.newInstance()
			}else{
				headModel=value
			}
			if(value!=null && attributes!=null){
				setProperties(headModel,attributes)
			}
			dimension.headModels << headModel
			this.headModel=headModel
			return 'headModel'
		}
		if(current=='headModel'){
			if(headModel instanceof Map){
				headModel[name]=value
			}else{
				setProperty(headModel,name,value,attributes)
			}
			return null
		}
		
		if(name=='nameStrategy'){
			def nameStrategy=parseNameStrategy(value,attributes)
			if(nameStrategy==null){
				return null
			}
			dimension.nameStrategy=nameStrategy
			return nameStrategy
		}
		if(name=='subDimension'){
			def subDimension
			if(value==null){
				subDimension=new Dimension()
			}else if(value instanceof String){
				def registrarClass=nameRegistrar.dimensionClasses[value]
				if(registrarClass){
					subDimension=registrarClass.newInstance()
				}else{
					logUnknownValue(name,value)
					return null
				}
			}else if(value instanceof Dimension){
				subDimension=value
			}else if(value instanceof Class){
				subDimension=value.newInstance()
			}else{
				logUnknownValue(name,value)
				return null
			}
			if(attributes!=null){
				setProperties(subDimension,attributes)
			}
			if(subDimension==null){
				return null
			}
			dimension.subDimension=subDimension
			return subDimension
		}
		
		setProperty(dimension,name,value,attributes)
		
		if(name=='staticHeads' && dimension.staticHeads==null){
			dimension.staticHeads=[]
		}else if(name=='headModels' && dimension.headModels==null){
			dimension.headModels=[]
		}
		node
	}
}
