package net.jyreport.core

/**
 *
 */
class Dimension implements Serializable,Cloneable {
	
	Class modelClass
	
	String tableName
	
	def headProcessor
	
	String name
	
	String text
	
	boolean dummy
	
	boolean hierarchical
	
	boolean fetchParents
	
	/****** 静态表头设置 *******/
	
	List<Head> staticHeads
	
	List headModels
	
	Closure modelSorter
	
	/****** 表头查询设置 *******/
	
	String fields
	
	boolean headsByData
	
	boolean headsSortByData
	
	/****** 数据查询设置 *******/
	
	String appendInParam='ifPaginated' // yes|no|ifPaginated
	
	String appendBetweenParam='no' // yes|no|ifPaginated
	
	Closure dataRequestCallback // call(report.dataRequests)
	
	Closure paramsCallback // eg. call(params,context,runtimeContext,sql)
	
	/****** head model属性名设置 *******/
	
	String parentProperty='parent'
	
	String parentIdProperty='parentId'
	
	String idProperty='id'
	
	String valueProperty='id'
	
	String nameProperty='name'
	
	String orderProperty=null
	
	def nameStrategy
	
	/****** 子维度设置 *******/
	
	Dimension subDimension
	
	//set at runtime
	Dimension parentDimension
	
	boolean carryParentParam=true
	
	Closure appendSubHeadCallback //call(thisHead,subHead) -> boolean
	
	Closure subDimensionExpandParams //call(thisHead) -> Map or [Map,String cacheKey]
	
	/****** 次级维度设置 *******/
	
	//set at runtime
	Dimension lowerDimension
	
	//set at runtime
	Dimension higherDimension
	
	Closure appendLowerHeadCallback //call(thisHead,lowerHead) -> boolean
	
	Closure lowerDimensionExpandParams //call(thisHead) -> Map or [Map,String cacheKey]
	
	/*************/
	
	String getText(){
		text ?: getName()
	}
	
	String getName(model){
		if(model==null){
			return null
		}
		model[getNameProperty()]
	}
	
	def getValue(model){
		if(model==null){
			return null
		}
		model[getValueProperty()]
	}
	
	def getId(model){
		if(model==null){
			return null
		}
		model[getIdProperty()]
	}
	
	String tableName(){
		getTableName() ?: getName()
	}
    
	List<Head> staticHeads(){
		List<Head> shs=getStaticHeads()
		if(shs==null){
			List hms=getHeadModels()
			if(hms!=null){
				setStaticHeads(wrapHeads(hms))
			}
		}else{
			shs.each{
				it.dimension=this
				if(it.model==null){
					it.model=[(getNameProperty()):it.name]
				}else if(it.name==null){
					it.name=getName(it.model)
				}
			}
		}
		return getStaticHeads()
	}
	
	List<Head> wrapHeads(List<Object> models){
		
		List<Head> heads=models.collect { model ->
			if(model instanceof Head){
				model.dimension=this
				return model
			}
			new Head(model:model,dimension:this,name:getName(model))
		}
		
		if(!isHierarchical()){
			return heads
		}
		
		//build hierarchies
		
		Map<Object,Head> modelToHead=heads.collectEntries{ head ->
			[head.model,head]
		}
		
		def idToModel
		def pid=getParentIdProperty()
		def pp=getParentProperty()
		if(pid){
			idToModel=models.collectEntries{
				[getId(it),it]
			}
		}
		models.each{ model ->
			def parent=null
			if(pid){
				parent=idToModel[model[pid]]
			}
			if(parent==null && pp){
				parent=model[pp]
			}
			if(parent==null){
				return
			}
			def childHead=modelToHead[model]
			def parentHead=modelToHead[parent]
			if(parentHead==null){
				return
			}
			parentHead.addChild(childHead)
		}
		
		// order
		List<Head> heads2=[]
		def appendHead
		appendHead={ head ->
			heads2 << head
			head.children?.each{appendHead(it)}
		}
		heads.grep{it.parent==null}.each{appendHead(it)}
		
		return heads2
	}
	
	Object clone(){
		//TODO:
		super.clone()
	}
}

