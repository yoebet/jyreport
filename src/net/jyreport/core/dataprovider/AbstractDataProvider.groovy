package net.jyreport.core.dataprovider

import net.jyreport.core.*
import net.jyreport.core.grid.*

/**
 *
 */
abstract class AbstractDataProvider implements DataProvider {
	
	DataRequest dataRequest
	
	Report report

	def config
	
	def runtimeContext
	
	def initialize(){
	}

	protected void appendParams(params){
	}
	
	protected evalTableDataModel(dataRequest){
		def tdm=dataRequest.tableDataModel ?: config.tableDataModel
		if(tdm!=null){
			return tdm.newInstance()
		}
		return new TableDataModel()
	}
	
	protected TableDataModel fromStaticDataModels(dataModels){
		DataTable dataTable=dataRequest.dataTable
		def tdm=evalTableDataModel(dataRequest)
		tdm.dataRequest=dataRequest
		tdm.implitParams=[:]
		tdm.dimensions=dataTable.allDimensions
		tdm.dataModels=dataTable.dataModels
		return tdm
	}
	
	abstract TableDataModel selectData()

	void close(){
    }
	
}

