package net.jyreport.core

import net.jyreport.core.grid.*
import net.jyreport.core.datatype.*

/**
 *
 */
class Report implements Serializable,Cloneable {
	
	String code
	
	String name
	
	/**
	 * 数据频度（用于取上期数）
	 * D：日；W：周；T：旬；M：月；Q：季；S：半年；Y：年
	 */
	String frequency
	
	boolean rowFormulaFirst
	
	Display display
	
	List<Dimension> dimensions
	
	Layout layout
	
	DataType dataType
	
	List<DataRequest> dataRequests
	
	Map<String,DataCriteria> headCriterias
	
	Map<String,List<Head>> derivedHeads
	
	//selector -> grid template
	Map<Object,DataGrid> dataGrids
	
	Instrument instrument
	
	Map<String,Dimension> dimensionMap
	
	//解析单元格取数请求，发现新的DataRequest
	boolean discoverDR
	
	/*****************************/
	
	//inject at runtime
	Map<String,Object> context
	
	//transfer to view
	Map<String,Object> properties
	
	/*****************************/
	
	def initialize(){
		
		if(layout==null && dimensions!=null){
			if(dimensions.size()==2){
				layout=new Layout(rd:dimensions[0],cd:dimensions[1])
			}else{
				throw new RuntimeException("未定义报表布局（layout）！")
			}
		}else if(layout!=null && dimensions==null){
			dimensions=layout.rds+layout.cds
		}
		
		def setDimensionHier
		setDimensionHier={dimension,subDimension->
			subDimension.parentDimension=dimension
			if(subDimension.subDimension){
				setDimensionHier(subDimension,subDimension.subDimension)
			}
		}
		dimensionMap=[:]
		dimensions.each{
			if(it.dummy && it.modelClass==null){
				it.modelClass=HashMap
			}
			if(it.subDimension){
				setDimensionHier(it,it.subDimension)
			}
			dimensionMap[it.name]=it
		}
		
		def setDimensionsGrade={ dims->
			if(dims.size()>1){
				1.upto(dims.size()-1) {i->
					def hd=dimensionMap[dims[i-1]]
					def ld=dimensionMap[dims[i]]
					hd.lowerDimension=ld
					ld.higherDimension=hd
				}
			}
		}
		setDimensionsGrade(layout.rowDimensions)
		setDimensionsGrade(layout.colDimensions)
		
		dataRequests.each{dataRequest->
			evalParams(dataRequest.params)
		}
		headCriterias?.each{dim,headCriteria->
			evalParams(headCriteria.params)
		}
		
		Map dataRequestMap=dataRequests.collectEntries{
			[it.name,it]
		}
		
		context.pagers?.each{ dim,pager ->
			if(dim.startsWith('data.')){
				def dataRequest=dataRequestMap[dim[5..-1]]
				if(dataRequest.pagination==null){
					dataRequest.pagination=new Pagination()
				}
				evalPagination(pager,dataRequest.pagination)
			}else{
				if(headCriterias==null){
					headCriterias=[:]
				}
				def headCriteria=headCriterias[dim]
				if(headCriteria==null){
					headCriteria=headCriterias[dim]=new DataCriteria()
				}
				if(headCriteria.pagination==null){
					headCriteria.pagination=new Pagination()
				}
				evalPagination(pager,headCriteria.pagination)
			}
		}
		
		if(dataType==null){
			dataType=new CurrencyType()
		}
		
		if(display==null){
			display=new Display()
		}
		def displaySetting=context.display
		displaySetting?.each{name,value ->
			if(display.hasProperty(name)){
				try{
					display."${name}"=value
				}catch(e){
					println e
				}
			}else{
				println "display does not have property: $name"
			}
		}
	}
	
	void evalParams(params){
		params?.each{k,v->
			if(v instanceof Closure){
				v.delegate=this
				params[k]=v.call()
			}else if(v=='CONTEXT[]' || v=='context[]'){
				params[k]=context[k]
			}
		}
	}
	
	void evalPagination(tpager,pagination){
		
		def perPage=tpager.perPage
		def page=tpager.page
		
		if(perPage){
			if(perPage instanceof Closure){
				perPage.delegate=this
				pagination.setPerPage(perPage.call().toInteger())
			}else{
				pagination.setPerPage(perPage.toInteger())
			}
		}
		if(page){
			if(page instanceof Closure){
				page.delegate=this
				pagination.setPage(page.call().toInteger())
			}else{
				pagination.setPage(page.toInteger())
			}
		}
	}
	
	void setDataRequest(dataRequest){
		if(dataRequests==null){
			dataRequests=[dataRequest]
		}else if(dataRequests.contains(dataRequest)){
			dataRequests << dataRequest
		}
	}
	
	DataRequest getDefaultDataRequest(){
		dataRequests[0]
	}
	
	void setDims(ds){
		dimensions=[]
		ds.each{
			def d=dimension(it)
			if(d){
				dimensions << d
			}
		}
	}
	
	protected Dimension dimension(dim){
		if(dim instanceof Class){
			return dim.newInstance()
		}else if(dim instanceof Dimension){
			return dim
		}else if(dim instanceof String){
			//TODO:...
		}else{
			println "dim: $dim"
		}
	}
	
	protected String dimensionName(dim){
		if(dim instanceof Class){
			return dim.newInstance().name
		}else if(dim instanceof String){
			return dim
		}else if(dim instanceof Dimension){
			return dim.name
		}else{
			println "dim: $dim"
			return null
		}
	}
	
	Object clone(){
		
		def report2=super.clone()
	
		report2.context=getContext()?.clone()
		report2.display=getDisplay()?.clone()
		report2.dataType=getDataType()?.clone()
		report2.layout=getLayout()?.clone()
		report2.dimensions=getDimensions()?.collect{it.clone()}
		report2.dataRequests=getDataRequests()?.collect{it.clone()}
		report2.headCriterias=getHeadCriterias()?.collectEntries{dim,criteria->
			[dim,criteria.clone()]
		}
		
//	derivedHeads
//	dataGrids
//	instrument
//	dimensionMap
//	properties
		
		report2
	}
	
}

