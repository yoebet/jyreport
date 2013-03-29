package net.jyreport.core.resultbuilder

import groovy.json.*
import net.jyreport.core.*

/**
 *
 */
class JsonResultBuilder implements ResultBuilder {
	
	boolean pretty=false
	
	def build(ReportData reportData){
		
		def json = new JsonBuilder()
		
		def report=reportData.report
		def display=report.display
		def dataRequest=report.dataRequests[0]
		def params=dataRequest.params ?: [:]
		
		def pags=[:]
		report.headCriterias.each { dimensionName,headCriteria ->
			def pag=headCriteria.pagination
			if(pag){
				pags[dimensionName]=[perPage:pag.perPage,pages:pag.pages,page:pag.page]
			}
		}
		
		def renderHeadAndGrids
		renderHeadAndGrids={ headGrid ->
			def hoc=[id:headGrid.id,text:headGrid.text,layer:headGrid.layer,
				derived:headGrid.derived]
			if(headGrid.parent != null){
				hoc.parentId=headGrid.parent.id
			}
			if(headGrid.properties != null){
				hoc.properties=headGrid.properties
			}
			if(headGrid.isRowHead){
				hoc.rows=headGrid.rows.size()
			}else{
				hoc.cols=headGrid.cols.size()
			}
			if(headGrid.lowerHeadGrids){
				hoc.lowerHeads=headGrid.lowerHeadGrids.collect{ lhg ->
					renderHeadAndGrids(lhg)
				}
			} else if(headGrid.isRowHead){
				hoc.grids=headGrid.grids.collect { grid ->
					def gridMap=[id:grid.id,value:grid.value,text:grid.text]
					if(grid.parent != null){
						gridMap.parentId=grid.parent.id
					}
					if(grid.properties != null){
						gridMap.properties=grid.properties
					}
					def dataType=grid.dataType
					if(dataType !=null && dataType != report.dataType){
						gridMap.dataType=dataType.name
					}
					gridMap
				}
			}
			return hoc
		}
		
		def hrhg=reportData.highestRowHeadGrids
		def hchd=reportData.highestColHeadGrids
		def rowsData=hrhg.collect{renderHeadAndGrids(it)}
		def colsData=hchd.collect{renderHeadAndGrids(it)}
		
		def dimensionNameMap=report.dimensions.collectEntries{[it.name,it]}
		
		json.report {
			meta {
				code report.code
				name report.name
				date params['date']?.format('yyyyMMdd')
				organ params['organ']
				currency params['currency']//TODO:
				unit display.currencyUnit
				dataType report.dataType?.name
				rows reportData.rowsCount
				cols reportData.colsCount
				layout {
					rows report.layout.rowDimensions.collect {
						def dim=dimensionNameMap[it]
						[name:it,text:dim.text,hierarchical:dim.hierarchical]
					}
					cols report.layout.colDimensions.collect {
						def dim=dimensionNameMap[it]
						[name:it,text:dim.text,hierarchical:dim.hierarchical]
					}
				}
				properties report.properties
			}
			
			paginations pags
			
			rows rowsData
			
			cols colsData
			
		}
		
		if(pretty){
			return json.toPrettyString()
		}else{
			return json.toString()
		}
	}
}

