package net.jyreport.core

import net.jyreport.core.grid.*
import net.jyreport.core.support.*

/**
 * TODO: Serialize
 */
class ReportData implements Serializable {
	
	Report report
	
	def config
	
	def runtimeContext
	
	List<HeadGrid> highestRowHeadGrids
	
	List<HeadGrid> highestColHeadGrids
	
	List<HeadGrid> lowestRowHeadGrids
	
	List<HeadGrid> lowestColHeadGrids
	
	List<List<DataGrid>> dataGrids
	
	int rowsCount=0
	
	int colsCount=0
	
	/******************/
	
	List<DataGrid> allDataGrids
	
	Map<String,List<TableDataModel>> tableDataModelsMap
	
	String defaultDataRequestName
	
	/******************/
	
	Map<String,HeadGrid> headsValueToGridMap
	
	Map<String,Map<Object,List<HeadGrid>>> dimensionToValueToGridsMap
	
	static String DATA_REQUEST_KEY='dataRequest'
	
	/******************/
	
	def fetchValue={ Map<String,Object> params, Closure aggregator=null ->
		List<TableDataModel> tdms=tableDataModelsMap[defaultDataRequestName]
		if(params!=null){
			String dataRequest=params[DATA_REQUEST_KEY]
			if(dataRequest!=null){
				tdms=tableDataModelsMap[dataRequest]
			}
		}
		def tableDataModel=tdms?.find {
			it.match(params)
		}
		if(tableDataModel!=null){
			tableDataModel.dataRequest.hitCount++
			return tableDataModel?.getValue(params,aggregator)
		}else{
			println "data model missing! $params"
			return null
		}
	}
	
	def initializeGrids(){
		
		traverseHeadGrids{it.init()}
		
		allDataGrids=[]
		traverseDataGrids{
			it.init()
			allDataGrids << it
		}
		
		buildHeadsKeyToGridMap()
		buildDimensionToValueToGridsMap()
	}
	
	def postInitializeGrids(){
		traverseDataGrids{
			it.initializedCallback?.call()
		}
	}
	
	def traverseHeadGrids(callback){
		def traverse
		traverse={ headGrids,cb ->
			headGrids.each{ headGrid ->
				cb(headGrid)
				if(headGrid.lowerHeadGrids){
					traverse(headGrid.lowerHeadGrids,cb)
				}
			}
		}
		[highestRowHeadGrids,highestColHeadGrids].each { headGrids ->
			traverse(headGrids,callback)
		}
	}
	
	def traverseDataGrids(callback){
		dataGrids.each{ rowGrids ->
			rowGrids.each{callback(it)}
		}
	}
	
	def buildHeadsKeyToGridMap(){
		headsValueToGridMap=[:]
		traverseDataGrids {
			headsValueToGridMap[it.headsValue]=it
		}
	}
	
	def buildDimensionToValueToGridsMap(){
		dimensionToValueToGridsMap=[:].withDefault{[:].withDefault{[]}}
		traverseHeadGrids { headGrid ->
			def head=headGrid.head
			//			if(head.aggregate){
			//				return
			//			}
			def valueToGridsMap=dimensionToValueToGridsMap[head.dimension.name]
			valueToGridsMap[head.value] << headGrid
		}
	}
	
	private def formulaCategories(){
		def cats=[NumberCategory,FormulaCategory]
		def cfc=config.formulaCategory
		if(cfc && cfc != FormulaCategory){
			if(cfc instanceof List){
				cats.addAll(cfc)
			}else{
				cats << cfc
			}
		}
		cats
	}
	
	def evaluateGrids(){
		use(formulaCategories()){
			traverseDataGrids { grid ->
				try{
					grid.evaluate()
				}catch(e){
					println "grid($grid): $e"
				}
			}
		}
	}
	
	def postEvaluateGrids(){
		Display display=report.display
		traverseDataGrids{ grid ->
			try{
				if(grid.evaluatedCallback){
					grid.evaluatedCallback.call()
				}
				grid.text=grid.dataType?.formatValue(grid.value,display)
			}catch(e){
				println "grid($grid): $e"
			}
		}
	}
	
	def evaluateDataRequests(fetchValueCallback){
		def oriFetchValue=fetchValue
		fetchValue=fetchValueCallback
		traverseDataGrids {it.tryEvaluateBegin()}
		use(formulaCategories()){
			traverseDataGrids { grid ->
				grid.evaluate()
			}
		}
		traverseDataGrids {it.tryEvaluateDone()}
		fetchValue=oriFetchValue
	}
	
	private filterHeadGrids(rhg,hhg,highestHeadGrids){
		hhg.lowerHeadGrids.remove(rhg)
		while(hhg && hhg.lowerHeadGrids.size()==0){
			rhg=hhg
			hhg=hhg.higherHeadGrid
			if(hhg){
				hhg.lowerHeadGrids.remove(rhg)
			}else{
				highestHeadGrids.remove(rhg)
			}
		}
	}
	
	def filterRows(callback){
		for(int i=rowsCount-1;i>=0;i--){
			def rowHeadGrid=lowestRowHeadGrids[i]
			def rowDataGrids=dataGrids[i]
			def filterOut=callback(rowHeadGrid,rowDataGrids)
			if(filterOut){
				lowestRowHeadGrids.remove(i)
				def rhg=rowHeadGrid
				def hhg=rhg.higherHeadGrid
				if(hhg){
					filterHeadGrids(rhg,hhg,highestRowHeadGrids)
				}else{
					highestRowHeadGrids.remove(i)
				}
				dataGrids.remove(i)
				rowsCount--
			}
		}
	}
	
	def filterColumns(callback){
		for(int i=colsCount-1;i>=0;i--){
			def colHeadGrid=lowestColHeadGrids[i]
			def colDataGrids=dataGrids*.get(i)
			def filterOut=callback(colHeadGrid,colDataGrids)
			if(filterOut){
				lowestColHeadGrids.remove(i)
				def rhg=colHeadGrid
				def hhg=rhg.higherHeadGrid
				if(hhg){
					filterHeadGrids(rhg,hhg,highestColHeadGrids)
				}else{
					highestColHeadGrids.remove(i)
				}
				dataGrids*.remove(i)
				colsCount--
			}
		}
	}
	
	/******************/
	
	DataGrid grid(int row,int col){
		if(row < rowsCount){
			def rowGrids=dataGrids[row]
			if(col < colsCount){
				return rowGrids[col]
			}
		}
		return null
	}
	
	DataGrid grid(String rowHeadKey,String colHeadKey){
		def headsValue=DataGrid.combinedValues([rowHeadKey],[colHeadKey])
		return headsValueToGridMap[headsValue]
	}
	
	DataGrid grid(Map<String,String> dimensionToHeadValue){
		def layout=report.layout
		def rowHeadKeys=layout.rowDimensions.collect {dimensionToHeadValue[it]}
		def colHeadKeys=layout.colDimensions.collect {dimensionToHeadValue[it]}
		def headsValue=DataGrid.combinedValues(rowHeadKeys,colHeadKey)
		return headsValueToGridMap[headsValue]
	}
	
	List<DataGrid> rowsGrid(rows, Closure headGridCondition){
		
		if(headGridCondition==null){
			return grids(rows,null)
		}
		
		if(rows==null){
			rows=0..<rowsCount
		}else if(rows instanceof Integer){
			rows=[rows]
		}else if(rows instanceof IntRange){
			rows=processRange(rows,rowsCount)
		}
		
		List<DataGrid> grids=[]
		
		headGridCondition.resolveStrategy=Closure.DELEGATE_FIRST
		rows.each { idx ->
			def headGrid=lowestRowHeadGrids[idx]
			headGridCondition.delegate=headGrid
			if(headGridCondition.call()){
				grids+=headGrid.grids
			}
		}
		
		return grids
	}
	
	List<DataGrid> colsGrid(cols, Closure headGridCondition){
		
		if(headGridCondition==null){
			return grids(null,cols)
		}
		
		if(cols==null){
			cols=0..<colsCount
		}else if(cols instanceof Integer){
			cols=[cols]
		}else if(cols instanceof IntRange){
			cols=processRange(cols,colsCount)
		}
		
		List<DataGrid> grids=[]
		
		headGridCondition.resolveStrategy=Closure.DELEGATE_FIRST
		cols.each { idx ->
			def headGrid=lowestColHeadGrids[idx]
			headGridCondition.delegate=headGrid
			if(headGridCondition.call()){
				grids+=headGrid.grids
			}
		}
		
		return grids
	}
	
	List<DataGrid> rowsGrid(rows){
		grids(rows,null)
	}
	
	List<DataGrid> colsGrid(cols){
		grids(null,cols)
	}
	
	private processRange(range,totalCount){
		int fi=range.fromInt
		int ti=range.toInt
		if(fi<0 && ti>0){
			int nti=totalCount+fi
			return (ti..nti)
		}
		return range
	}
	
	List<DataGrid> grids(rows, cols){
		if(rows==null){
			if(cols==null){
				return allDataGrids
			}
			if(cols instanceof Integer){
				//cols can be negative
				HeadGrid colHeadGrid=lowestColHeadGrids[cols]
				return colHeadGrid.grids
			}
			rows=0..<rowsCount
		}else if(rows instanceof Integer){
			if(cols==null){
				//rows can be negative
				return dataGrids[rows]
			}
			rows=[rows]
		}else if(rows instanceof IntRange){
			rows=processRange(rows,rowsCount)
		}
		if(cols==null){
			cols=0..<colsCount
		}else if(cols instanceof Integer){
			cols=[cols]
		}else if(cols instanceof IntRange){
			cols=processRange(cols,colsCount)
		}
		List<DataGrid> grids=[]
		rows.each { row ->
			if(row>=rowsCount)return
			cols.each { col ->
				if(col>=colsCount)return
				grids << dataGrids[row][col]
			}
		}
		return grids
	}
	
	List<DataGrid> grids(Map<String,Object> dimensionToHeadValue){
		Set rowIndexes
		Set colIndexes
		def dimensions=dimensionToHeadValue.keySet()
		def layout=report.layout
		def rowDimensions=layout.rowDimensions.grep {dimensions.contains(it)}
		def colDimensions=layout.colDimensions.grep {dimensions.contains(it)}
		rowDimensions.each{ dimension ->
			def headValues=dimensionToHeadValue[dimension]
			if(headValues instanceof String || headValues instanceof GString){
				headValues=[headValues]
			}else{
				headValues=headValues as List
			}
			def valueToGridsMap=dimensionToValueToGridsMap[dimension]
			def theRows=[]
			headValues.each { headValue ->
				def grids=valueToGridsMap[headValue]
				grids.each{ theRows.addAll it.rows}
			}
			if(rowIndexes==null){
				rowIndexes=theRows
			}else{
				rowIndexes=rowIndexes.intersect(theRows)
			}
		}
		colDimensions.each{ dimension ->
			def headValues=dimensionToHeadValue[dimension]
			if(headValues instanceof String || headValues instanceof GString){
				headValues=[headValues]
			}else{
				headValues=headValues as List
			}
			def valueToGridsMap=dimensionToValueToGridsMap[dimension]
			def theCols=[]
			headValues.each { headValue ->
				def grids=valueToGridsMap[headValue]
				grids.each{ theCols.addAll it.cols}
			}
			if(colIndexes==null){
				colIndexes=theCols
			}else{
				colIndexes=colIndexes.intersect(theCols)
			}
		}
		
		return grids(rowIndexes?.sort(),colIndexes?.sort())
	}
}

