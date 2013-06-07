package net.jyreport.core

import net.jyreport.core.grid.*
import net.jyreport.core.dimension.*
import net.jyreport.core.headprocessor.*
import net.jyreport.core.dataprovider.*
import net.jyreport.core.selector.*
import net.jyreport.core.support.*

/**
 *
 */
class ReportProcessor {
	
	Report report
	
	ReportData reportData
	
	Map<String,HeadProcessor> headProcessors
	
	Map<String,List<Head>> dimensionsHeadMap
	
	Map<String,List<Object>> headValuesMap
	
	Map<String,List<DataRequest>> dataRequestsMap
	
	Map<String,Dimension> dimensionMap
	
	def config
	
	def runtimeContext
	
	def instrument
	
	Map<String,Map> subDimensionHeadsCache=[:].withDefault{[:]}
	
	Map<String,Map> lowerDimensionHeadsCache=[:].withDefault{[:]}
	
	
	def setupHeadProcessors={
		headProcessors=[:]
		
		def buildProcessor
		buildProcessor={ dimension ->
			def pc=dimension.headProcessor ?: config.headProcessor
			def processor=setupHeadProcessor(pc,dimension)
			headProcessors[dimension.name]=processor
			
			Dimension subDimension=dimension.subDimension
			if(subDimension){
				buildProcessor(subDimension)
				dimensionMap[subDimension.name]=subDimension
			}
		}
		report.dimensions.each { dimension ->
			buildProcessor(dimension)
		}
	}
	
	def expandHeads={
		dimensionsHeadMap=headProcessors.findAll{
			def dimension=dimensionMap[it.key]
			if(dimension.parentDimension){
				def parentDimension=dimension.parentDimension
				if(parentDimension.subDimensionExpandParams!=null){
					return false
				}
			}
			if(dimension.higherDimension){
				def higherDimension=dimension.higherDimension
				if(higherDimension.lowerDimensionExpandParams!=null){
					return false
				}
			}
			return !dimension.headsByData
		}.collectEntries { dimensionName,headProcessor->
			def heads=headProcessor.expand()
			[dimensionName,heads]
		}
		
	}
	
	
	def expandDynamicHeads={ subDimension,paramsAndKey,headsCacheMap ->
		Map subDimParams
		def cacheKey=null
		if(paramsAndKey instanceof List){
			subDimParams=paramsAndKey[0]
			cacheKey=paramsAndKey[1]
		}else{
			subDimParams=paramsAndKey
		}
		if(cacheKey==null){
			if(subDimParams){
				cacheKey=subDimParams.collect{name,value->
				"$name->$value"
				}.join(',')
			}else{
				cacheKey='null'
			}
		}
		def subHeads=headsCacheMap[cacheKey]
		if(subHeads){
			return subHeads
		}
		
		def headProcessor=headProcessors[subDimension.name]
		headProcessor.extraParams=subDimParams
		subHeads=headProcessor.expand()
		
		headsCacheMap[cacheKey]=subHeads
		
		return subHeads
	}
	
	List<Head> expandSubHeads(subDimension,parentDimension,parentHead){
		def paramsAndKey=parentDimension.subDimensionExpandParams.call(parentHead)
		if(paramsAndKey==null){
			return null
		}
		def headsCacheMap=subDimensionHeadsCache[subDimension.name]
		expandDynamicHeads(subDimension,paramsAndKey,headsCacheMap)
	}
	
	List<Head> expandLowerHeads(lowerDimension,higherDimension,higherHead){
		def paramsAndKey=higherDimension.lowerDimensionExpandParams.call(higherHead)
		def headsCacheMap=lowerDimensionHeadsCache[lowerDimension.name]
		expandDynamicHeads(lowerDimension,paramsAndKey,headsCacheMap)
	}
	
	def processSubDimensionHeads={
		
		def addSubDimensionHeads
		addSubDimensionHeads={ dimension,subDimension ->
			
			if(subDimension.subDimension){
				addSubDimensionHeads(subDimension,subDimension.subDimension)
			}
			
			List<Head> subHeads
			if(dimension.subDimensionExpandParams==null){
				subHeads=dimensionsHeadMap[subDimension.name]
				if(!subHeads){
					return
				}
			}
			
			def appendSubHeadCallback=dimension.appendSubHeadCallback
			
			def derivedHeads
			if(report.derivedHeads){
				derivedHeads=report.derivedHeads[subDimension.name]
			}
			
			List<Head> heads=dimensionsHeadMap[dimension.name]
			heads.each{ head->
				if(head.children){
					return
				}
				//动态获取子维度
				if(dimension.subDimensionExpandParams!=null){
					subHeads=expandSubHeads(subDimension,dimension,head)
					if(subHeads==null){
						return
					}
					if(derivedHeads){
						appendDerivedHead(subDimension,subHeads,derivedHeads)
					}
				}
				subHeads.each{ subHead->
					if(appendSubHeadCallback!=null){
						if(appendSubHeadCallback.call(head,subHead)){
							head.addChild(subHead.clone())
						}
					}else{
						head.addChild(subHead.clone())
					}
				}
			}
		}
		report.dimensions.each { dimension ->
			if(!dimension.subDimension){
				return
			}
			addSubDimensionHeads(dimension,dimension.subDimension)
			List<Head> heads=dimensionsHeadMap[dimension.name]
			
			List<Head> heads2=[]
			def appendHead
			appendHead={ head ->
				heads2 << head
				head.children?.each{appendHead(it)}
			}
			heads.grep{it.parent==null}.each{appendHead(it)}
			
			dimensionsHeadMap[dimension.name]=heads2
		}
	}
	
	def expandHeadsByData={
		headProcessors.findAll{
			def dimension=dimensionMap[it.key]
			dimension.headsByData
		}.each{dimensionName,headProcessor->
			def seq=1
			def sortMap=[:]
			Set headValues=[]
			reportData.tableDataModelsMap.each{name,tableDataModels->
				tableDataModels.each{tdm->
					tdm.dataModels.each{rowModel->
						def headValue=rowModel[dimensionName]
						headValues << headValue
						sortMap[headValue]=seq++
					}
				}
			}
			def params=headProcessor.headCriteria.params
			if(params==null){
				params=headProcessor.headCriteria.params=[:]
			}
			def dimension=dimensionMap[dimensionName]
			params[dimension.valueProperty]=['in',headValues]
			
			//sort by data
			if(dimension.headsSortByData){
				dimension.modelSorter={ model ->
					def value=dimension.getValue(model)
					sortMap[value]
				}
			}
			
			def heads=headProcessor.expand()
			dimensionsHeadMap[dimensionName]=heads
		}
	}
	
	def appendDerivedHead(dimension,heads,derivedHeads){
		derivedHeads?.each { derivedHead ->
			derivedHead.dimension=dimension
			derivedHead.derived=true
			Grid templateGrid=derivedHead.templateGrid
			if(templateGrid){
				templateGrid.derived=true
				templateGrid.head=derivedHead
			}
			if(derivedHead.name==null){
				derivedHead.name=derivedHead.model?.name
			}
			Integer position=derivedHead.position
			if(position!=null){
				if(position<0){
					position=heads.size+1+position
				}
				if(0 <= position && position <= heads.size){
					heads.add(position,derivedHead)
				}else{
					heads << derivedHead
				}
			}else{
				heads << derivedHead
			}
		}
	}
	
	def processDerivedHeads={
		report.derivedHeads?.each { dimensionName,derivedHeads ->
			List<Head> heads=dimensionsHeadMap[dimensionName]
			if(heads==null){
				return
			}
			appendDerivedHead(dimensionMap[dimensionName],heads,derivedHeads)
		}
	}
	
	def createHeadGrids={

		//		dimensionsHeadMap.each{ dimensionName,heads ->
		//			println "<$dimensionName>:"
		//			def printHead
		//			printHead={ head,layer ->
		//				print "	"*layer
		//				println head
		//				head.children?.each{printHead(it,layer+1)}
		//			}
		//			heads.grep{it.parent==null}.each{printHead(it,1)}
		//		}
		//		println ''
		
		def buildHierarchies={ headGrids ->
			if(headGrids.size()==0){
				return
			}
			def headGridsHasChildren=headGrids*.head.grep {it.children}
			if(headGridsHasChildren.size()==0){
				return
			}
			def headToGridMap=headGrids.collectEntries { headGrid ->
				[headGrid.head,headGrid]
			}
			headGridsHasChildren.each{ head ->
				def headGrid=headToGridMap[head]
				def headGridChildren=headGrid.children=[]
				head.children.each { childHead ->
					def childHeadGrid=headToGridMap[childHead]
					if(childHeadGrid){
						headGridChildren << childHeadGrid
						childHeadGrid.parent=headGrid
					}
				}
			}
		}
		
		def createHeadGrid={ head,isRowHead,higherHeadGrid=null ->
			def templateGrid=head.templateGrid
			HeadGrid hg=null
			if(templateGrid!=null){
				hg=templateGrid.clone()
			}else{
				hg=new HeadGrid()
			}
			hg.head=head
			hg.isRowHead=isRowHead
			hg.derived=head.derived
			hg.higherHeadGrid=higherHeadGrid
			return hg
		}
		
		def createLowerHeadGrids
		createLowerHeadGrids={ headGrid,dimensions,dimensionsHeads,lowerDimensionIndex,isRowHead ->
			if(lowerDimensionIndex >= dimensionsHeads.size()){
				return
			}
			def higherHead=headGrid.head
			def higherDimension=higherHead.dimension
			def lowerHeadGrids=headGrid.lowerHeadGrids=[]
			def lowerHeads=dimensionsHeads[lowerDimensionIndex]
			def lowerDimension=dimensions[lowerDimensionIndex]
			
			def genDummyHead={
				def dd=lowerDimension.clone()
				dd.dummy=true
				def dh=new Head(dimension:dd)
				if(higherHead.aggregate){
					dh.aggregate=true
				}
				dh.name=''
				return dh
			}
			if(higherHead.aggregate){
				lowerHeads=[genDummyHead()]
			}else{
				if(!lowerHeads){
					if(higherDimension.lowerDimensionExpandParams!=null){
						//生成次级维度表头
						lowerHeads=expandLowerHeads(lowerDimension,higherDimension,higherHead)
						if(report.derivedHeads){
							def derivedHeads=report.derivedHeads[lowerDimension.name]
							if(derivedHeads){
								appendDerivedHead(lowerDimension,lowerHeads,derivedHeads)
							}
						}
					}
				}
				if(!lowerHeads){
					lowerHeads=[genDummyHead()]
				}
			}
			
			def alhcb=higherDimension.appendLowerHeadCallback
			if(alhcb!=null && !higherHead.aggregate){
				lowerHeads=lowerHeads.grep{
					alhcb.call(higherHead,it)
				}
				if(lowerHeads.empty){
					lowerHeads=[genDummyHead()]
				}
			}
			lowerHeads.each { lowerHead ->
				HeadGrid hg=createHeadGrid(lowerHead,isRowHead,headGrid)
				lowerHeadGrids << hg
				createLowerHeadGrids(hg,dimensions,dimensionsHeads,lowerDimensionIndex+1,isRowHead)
			}
			buildHierarchies(lowerHeadGrids)
		}
		
		def layout=report.layout
		def highestRowHeadGrids=reportData.highestRowHeadGrids=[]
		def highestColHeadGrids=reportData.highestColHeadGrids=[]
		
		[layout.rowDimensions,layout.colDimensions].eachWithIndex { dimensionNames,index ->
			def dimensionsHeads=dimensionNames.collect {dimensionsHeadMap[it]}
			if(dimensionsHeads.empty){
				return
			}
			def headGrids=(index==0)? highestRowHeadGrids : highestColHeadGrids
			boolean isRowHead=index==0
			def dimension0=dimensionsHeads.first()
			dimension0.each { head ->
				headGrids << createHeadGrid(head,isRowHead)
			}
			buildHierarchies(headGrids)
			def dimensions=dimensionNames.collect {dimensionMap[it]}
			headGrids.each { hg ->
				createLowerHeadGrids(hg,dimensions,dimensionsHeads,1,isRowHead)
			}
		}
	}
	
	def createDataGrids={
		
		List<HeadGrid> lowestRowHeadGrids=reportData.lowestRowHeadGrids=[]
		List<HeadGrid> lowestColHeadGrids=reportData.lowestColHeadGrids=[]
		def appender
		appender={ appendTo,headGrids->
			headGrids.each { headGrid->
				if(headGrid.lowerHeadGrids){
					appender(appendTo,headGrid.lowerHeadGrids)
				}else{
					appendTo << headGrid
				}
			}
		}
		appender(lowestRowHeadGrids,reportData.highestRowHeadGrids)
		appender(lowestColHeadGrids,reportData.highestColHeadGrids)
		
		lowestColHeadGrids.each {
			it.grids=[]
		}
		
		lowestColHeadGrids.eachWithIndex { colHeadGrid,colIndex ->
			colHeadGrid.col=colIndex
			def hhg=colHeadGrid.higherHeadGrid
			while(hhg!=null && hhg.col==null){
				hhg.col=colIndex
				hhg=hhg.higherHeadGrid
			}
		}
		
		List<List<DataGrid>> dataGrids=reportData.dataGrids=[]
		
		lowestRowHeadGrids.eachWithIndex { rowHeadGrid,rowIndex ->
			rowHeadGrid.row=rowIndex
			def hhg=rowHeadGrid.higherHeadGrid
			while(hhg!=null && hhg.row==null){
				hhg.row=rowIndex
				hhg=hhg.higherHeadGrid
			}
			List<DataGrid> rowDadaGrids=[]
			rowHeadGrid.grids=rowDadaGrids
			dataGrids << rowDadaGrids
			
			lowestColHeadGrids.eachWithIndex { colHeadGrid,colIndex ->
				DataGrid dataGrid=new DataGrid()
				dataGrid.rowHeadGrid=rowHeadGrid
				dataGrid.colHeadGrid=colHeadGrid
				dataGrid.row=rowIndex
				dataGrid.col=colIndex
				dataGrid.derived=rowHeadGrid.derived||colHeadGrid.derived
				dataGrid.reportData=reportData
				colHeadGrid.grids << dataGrid
				rowDadaGrids << dataGrid
			}
		}
		
		reportData.rowsCount=dataGrids.size()
		reportData.colsCount=lowestColHeadGrids.size()
		
		//set dataGrid's parent/children
		[lowestRowHeadGrids,lowestColHeadGrids].eachWithIndex{ headGrids,index ->
			headGrids.grep{it.children}.each{ headGrid ->
				headGrid.grids.each{ parentDataGrid ->
					headGrid.children.each{ childHeadGrid ->
						def childDataGrid
						if(index==0){
							childDataGrid=parentDataGrid.rowAbs(childHeadGrid.row)
						}else{
							childDataGrid=parentDataGrid.colAbs(childHeadGrid.col)
						}
						parentDataGrid.addChild(childDataGrid)
					}
				}
			}
		}
		
	}
	
	
	def applyDataGrids={
		Map<Object,DataGrid> dataGrids=report.dataGrids
		dataGrids?.each { location,template ->
			Selector selector
			if(location instanceof Selector){
				selector=location
			}else{
				selector=new BasicSelector(location)
			}
			selector.reportData=reportData
			List<DataGrid> grids=selector.select()
			if(grids==null){
				return
			}
			
			grids?.each { grid ->
				//copy from template
				if(template.dataType){
					grid.dataType=template.dataType
				}
				if(template.value){
					grid.value=template.value
				}
				['params','properties'].each{mapName->
					if(template[mapName]){
						if(grid[mapName]){
							grid[mapName].putAll(template[mapName])
						}else{
							grid[mapName]=template[mapName]
						}
					}
				}
				if(template.evaluator){
					grid.evaluator=template.evaluator.clone()
					grid.evaluator.delegate=grid
				}
				if(template.aggregator){
					grid.setAggregator(template.aggregator)
				}
				if(template.initializedCallback){
					grid.setInitializedCallback(template.initializedCallback)
				}
				if(template.evaluatedCallback){
					grid.setEvaluatedCallback(template.evaluatedCallback)
				}
			}
		}
	}
	
	def processDataRequestParams={
		
		headValuesMap=[:]
		report.headCriterias.each { dimensionName,headCriteria ->
			List<Head> heads=dimensionsHeadMap[dimensionName]
			if(heads==null){
				println "维度名错误：${dimensionName}"
				return
			}
			if(heads.size()==0){
				return
			}
			//in case another dimension
			heads=heads.grep{
				it.dimension.name==dimensionName
			}
			
			def dimension=dimensionMap[dimensionName]
			def appendInParam=dimension.appendInParam
			def appendBetweenParam=dimension.appendBetweenParam
			def pagination=headCriteria.pagination
			
			if(appendInParam=='yes' || (appendInParam=='ifPaginated' && pagination)){
				headValuesMap[dimensionName]=heads.grep{!it.aggregate && !it.derived}*.value
			}
			if(appendBetweenParam=='yes' || (appendBetweenParam=='ifPaginated' && pagination)){
				def firstValue=dimension.getValue(heads.first().model)
				def lastValue=dimension.getValue(heads.last().model)
				report.dataRequests.each{
					it.params[dimension.name]=['between',[firstValue,lastValue]]
				}
			}
			
			if(dimension.dataRequestCallback){
				dimension.dataRequestCallback(report.dataRequests)
			}
		}
	}
	
	def parseDataRequest={
		dataRequestsMap=[:].withDefault{[] as List<DataRequest>}
		report.dataRequests.each {dataRequestsMap[it.name] << it}
		def defaultDataRequest=report.defaultDataRequest
		reportData.defaultDataRequestName=defaultDataRequest.name
		
		if(report.dimensions.find{it.headsByData}){
			//有表头由数据决定
			return
		}
		
		if(report.discoverDR){
			def defaultDrs=dataRequestsMap[defaultDataRequest.name]
			Set<String> headDimensions=dimensionMap.keySet()
			def fetchValueCallback={ Map<String,Object> params, Closure aggregator=null ->
				def dr=defaultDataRequest
				if(params!=null){
					def implicitParams=params.findAll{k,v-> !headDimensions.contains(k)}
					String dataRequestName=implicitParams[ReportData.DATA_REQUEST_KEY]
					def drs=defaultDrs
					if(dataRequestName!=null){
						drs=dataRequestsMap[dataRequestName]
					}
					def ndr=drs.find {
						it.match(implicitParams)
					}
					if(ndr){
						dr=ndr
					}else{
						def drs0=drs[0]
						DataRequest newDataRequest=drs0.clone()
						newDataRequest.params=drs0.params.clone()+implicitParams
						drs << newDataRequest
						dr=newDataRequest
					}
				}
				1
			}
			
			reportData.evaluateDataRequests(fetchValueCallback)
		}
	}
	
	def selectData={
		
		reportData.tableDataModelsMap=dataRequestsMap.collectEntries { name,dataRequests ->
			def tableDataModels=dataRequests.collect {dataRequest->
				if(!dataRequest.headValuesMap){
					dataRequest.headValuesMap=headValuesMap
				}
				DataTable dataTable=dataRequest.dataTable
				def dp=dataTable.dataProvider ?: config.dataProvider
				def dataProvider=setupDataProvider(dp,dataRequest)
				def tableDataModel=dataProvider.selectData()
				dataProvider.close()
				return tableDataModel
			}
			[name,tableDataModels]
		}
	}
	
	private setupHeadProcessor(pc,dimension){
		
		def headCriterias=report.headCriterias
		def processor=(pc instanceof Class)? pc.newInstance() : pc
		processor.dimension=dimension
		processor.report=report
		processor.config=config
		processor.runtimeContext=runtimeContext
		if(headCriterias){
			processor.headCriteria=headCriterias[dimension.name] ?: new DataCriteria()
		}else{
			processor.headCriteria=new DataCriteria()
		}
		processor.initialize()
		processor
	}
	
	private setupDataProvider(dp,dataRequest){
		def dataProvider=(dp instanceof Class)? dp.newInstance() : dp
		dataProvider.dataRequest=dataRequest
		dataProvider.report=report
		dataProvider.config=config
		dataProvider.runtimeContext=runtimeContext
		dataProvider.initialize()
		dataProvider
	}
	
	def setGridIds={
		
		int gid=1
		reportData.traverseHeadGrids {it.id=gid++}
		reportData.traverseDataGrids {it.id=gid++}
	}
	
	private void doInit(){
		
		if(runtimeContext==null){
			runtimeContext=[:]
		}
		
		reportData=new ReportData(report:report)
		reportData.config=config
		reportData.runtimeContext=runtimeContext
		
		report.initialize()
		def ris=report.instrument
		if(ris!=null){
			if(ris instanceof Class){
				instrument=ris.newInstance()
			}else{
				instrument=ris
			}
		}
		if(instrument!=null){
			instrument.reportProcessor=this
			instrument.reportData=reportData
		}
		
		dimensionMap=report.dimensionMap
	}
	
	ReportData buildReport(){
		
		doInit()
		
		instrument?.componentsInitialized()
		
		//有表头由数据决定
		Boolean headsByData=report.dimensions.find{it.headsByData}
		
		//确定表头处理器，扩展维度、处理表头
		setupHeadProcessors()
		expandHeads()
		processSubDimensionHeads()
		processDataRequestParams()
		
		if(headsByData){
			//解析数据请求，取数
			parseDataRequest()
			selectData()
			expandHeadsByData()
		}
		
		//插入派生行／列，创建表头单元格，创建数据单元格
		processDerivedHeads()
		
		instrument?.beforeCreateHeadGrids()

		headProcessors.each{ n,headProcessor ->
			headProcessor.close()
		}
		
		createHeadGrids()
		createDataGrids()
		
		//初始化，调整数据单元格，设置id
		reportData.initializeGrids()
		applyDataGrids()
		setGridIds()
		
		reportData.postInitializeGrids()
		instrument?.gridsInitialized()
		
		if(!headsByData){
			//解析数据请求，取数
			parseDataRequest()
			selectData()
		}
		
		//计算表格值
		reportData.evaluateGrids()
		reportData.postEvaluateGrids()
		instrument?.gridsEvaluated()
		
		return reportData
	}
}

