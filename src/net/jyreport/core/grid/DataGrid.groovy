package net.jyreport.core.grid

import net.jyreport.core.*
import net.jyreport.core.support.*
import net.jyreport.core.datatype.*

/**
 *
 */
class DataGrid extends Grid {

	HeadGrid rowHeadGrid

	HeadGrid colHeadGrid

	String text

	def headsValue

	Map<String,Object> dparams

	def formulaFrom

	Boolean rowFormulaFirst

	def init(){
		dparams=[:]

		def report=reportData.report

		def rowHeadGrids=[]
		def colHeadGrids=[]
		def rh=rowHeadGrid
		def ch=colHeadGrid
		def head
		def dimension
		
		def appendParentHeadParam={curHead->
			def parentHead=curHead.parent
			while(parentHead && curHead.carryParentHeadParam){
				dparams[parentHead.dimension.name]=parentHead.value
				curHead=parentHead
				parentHead=curHead.parent
			}
		}

		while(rh){
			head=rh.head
			dimension=head.dimension
			if(!head.aggregate && !dimension.dummy && head.value!=null){
				dparams[dimension.name]=head.value
			}
			rowHeadGrids.add(0,rh)
			def rhh=rh.head
			if(rhh.parent && rhh.carryParentHeadParam){
				appendParentHeadParam(rhh)
			}
			
			rh=rh.higherHeadGrid
		}
		while(ch){
			head=ch.head
			dimension=head.dimension
			if(!head.aggregate && !dimension.dummy && head.value!=null){
				dparams[dimension.name]=head.value
			}
			colHeadGrids.add(0,ch)
			def chh=ch.head
			if(chh.parent && chh.carryParentHeadParam){
				appendParentHeadParam(chh)
			}
			
			ch=ch.higherHeadGrid
		}
		def rowHeadKeys=rowHeadGrids*.head.grep{!it.aggregate}*.value
		def colHeadKeys=colHeadGrids*.head.grep{!it.aggregate}*.value
		headsValue=combinedValues(rowHeadKeys,colHeadKeys)

		if(params==null){
			params=[:]
		}
		params=dparams+rowHeadGrid.params+colHeadGrid.params+params

		if(dataType==null){
			dataType=colHeadGrid.dataType ?: rowHeadGrid.dataType ?: report.dataType
		}

		//determine which (formulaGrid1、formulaGrid2) first
		def formulaGrid1
		def formulaGrid2
		if(rowFormulaFirst==null){
			rowFormulaFirst=report.rowFormulaFirst
		}
		if(rowFormulaFirst){
			formulaGrid1=rowHeadGrid
			formulaGrid2=colHeadGrid
		}else{
			formulaGrid1=colHeadGrid
			formulaGrid2=rowHeadGrid
		}
		if(colHeadGrid.derived || rowHeadGrid.derived){
			if(colHeadGrid.derived && !rowHeadGrid.derived){
				formulaGrid1=colHeadGrid
				formulaGrid2=rowHeadGrid
			}else if(!colHeadGrid.derived && rowHeadGrid.derived){
				formulaGrid1=rowHeadGrid
				formulaGrid2=colHeadGrid
			}
		}
		
		if(evaluator==null){
			if(formulaGrid1.evaluator!=null){
				evaluator=formulaGrid1.evaluator.clone()
				formulaFrom=formulaGrid1
			}else if(formulaGrid2.evaluator!=null){
				evaluator=formulaGrid2.evaluator.clone()
				formulaFrom=formulaGrid2
			}else{
				evaluator={v()}
			}
		}
		evaluator.delegate=this
		evaluator.resolveStrategy=Closure.DELEGATE_FIRST
		
		if(formulaGrid1.aggregator){
			setAggregator(formulaGrid1.aggregator)
		}else if(formulaGrid2.aggregator!=null){
			setAggregator(formulaGrid2.aggregator)
		}
		if(formulaGrid1.initializedCallback){
			setInitializedCallback(formulaGrid1.initializedCallback)
		}else if(formulaGrid2.initializedCallback!=null){
			setInitializedCallback(formulaGrid2.initializedCallback)
		}
		if(formulaGrid1.evaluatedCallback){
			setEvaluatedCallback(formulaGrid1.evaluatedCallback)
		}else if(formulaGrid2.evaluatedCallback!=null){
			setEvaluatedCallback(formulaGrid2.evaluatedCallback)
		}
	}

	String getText(){
		if(value instanceof Formula){
			println "evaluate faild: $row.$col"
			return ''
		}
		text ?: value?.toString() ?: ''
	}

	DataGrid grid(int row,int col){
		reportData.grid(row,col)
	}

	DataGrid rowRel(int offset){
		grid(row+offset,col)
	}

	DataGrid colRel(int offset){
		grid(row,col+offset)
	}

	DataGrid rowAbs(int row){
		grid(row,col)
	}

	DataGrid colAbs(int col){
		grid(row,col)
	}

	DataGrid abs(int row,int col){
		reportData.grid(row,col)
	}

	DataGrid rel(int roffset,int coffset){
		grid(row+roffset,col+coffset)
	}

	DataGrid rr(int offset){
		grid(row+offset,col)
	}

	DataGrid cr(int offset){
		grid(row,col+offset)
	}

	DataGrid ra(int row){
		grid(row,col)
	}

	DataGrid ca(int col){
		grid(row,col)
	}

	DataGrid getHigher(){
		rr(-1)
	}

	DataGrid getLower(){
		rr(1)
	}

	DataGrid getLeft(){
		cr(-1)
	}

	DataGrid getRight(){
		cr(1)
	}
    
    List<DataGrid> thisRowGrids(Closure condition=null){
        def grids=reportData.rowsGrid(row)
		if(condition==null){
			return grids
		}
		condition.resolveStrategy=Closure.DELEGATE_FIRST
		return grids.grep{
			condition.delegate=it
			condition.call()
		}
    }
    
    List<DataGrid> thisColGrids(Closure condition=null){
        def grids=reportData.colsGrid(col)
		if(condition==null){
			return grids
		}
		condition.resolveStrategy=Closure.DELEGATE_FIRST
		return grids.grep{
			condition.delegate=it
			condition.call()
		}
    }
    

	def v(extra=null){
		def aparams=extra? (params+extra) : params
		v0(aparams)
	}
	
	def v0(aparams,aggregator=null){
        if(aggregator==null){
            aggregator=this.aggregator
        }
		def res=reportData.fetchValue(aparams,aggregator)
		if(res!=null){
			return res
		}
		if(dataType && dataType instanceof TextType){
			''
		}else{
			0
		}
    }

	//获取兄弟单元格
	def getSiblings(){
		if(formulaFrom!=colHeadGrid && formulaFrom!=rowHeadGrid){
			return []
		}

		def grids=[]

		def allRowsCount=reportData.rowsCount
		def allColsCount=reportData.colsCount

		if(formulaFrom.higherHeadGrid){
			def hhg=formulaFrom.higherHeadGrid
			while(hhg.head.aggregate){
				//aggregate可能是继承而来，找出最上一个
				def hhhg=hhg.higherHeadGrid
				if(hhhg){
					hhg=hhhg
				}else{
					break
				}
			}

			if(hhg.head.aggregate && hhg.higherHeadGrid==null){
				//对全表聚合
				if(hhg.isRowHead){
					allRowsCount.times {grids << rowAbs(it)}
				}else{
					allColsCount.times {grids << colAbs(it)}
				}
			}else{
				if(hhg.isRowHead){
					hhg.rows.each{grids << rowAbs(it)}
				}else{
					hhg.cols.each{grids << colAbs(it)}
				}
			}
		}else if(formulaFrom.parent){
			formulaFrom.parent.children.each{ headGrid ->
				if(formulaFrom.isRowHead){
					grids << rowAbs(headGrid.row)
				}else{
					grids << colAbs(headGrid.col)
				}
			}
		}else {
			//对全表聚合
			if(formulaFrom.isRowHead){
				allRowsCount.times {grids << rowAbs(it)}
			}else{
				allColsCount.times {grids << colAbs(it)}
			}
		}

		return grids.grep{!it.is(this)}

	}

	String toString(){
		"$row.$col: $value"
	}

	static combinedValues(rowHeadValues,colHeadValues){
		rowHeadValues.join(',')+","+colHeadValues.join(',')
	}

	static SUM={
		getSiblings()?.grep{!it.derived && !it.is(this)}.sum() ?: 0.0
	}

	static SUM_DERIVED={
		getSiblings()?.grep{it.derived && !it.is(this)}.sum() ?: 0.0
	}

	static SUM_ALL={
		getSiblings()?.grep{!it.is(this)}.sum() ?: 0.0
	}

	static SUM_CHILDREN={
		children?.grep{!it.derived}.sum() ?: 0.0
	}
	
	//evaluatedCallback
	static SET_PERCENT_TO_PARENT={
		if(value!=null && parent.value!=null && parent.value!=0.0){
			value=value/parent.value
		}else{
			value=0.0
		}
		if(!(dataType instanceof PercentType)){
			dataType=new PercentType()
		}
	}
}

