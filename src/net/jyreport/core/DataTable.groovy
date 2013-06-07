package net.jyreport.core

/**
 *
 */
class DataTable {
	
	String name
	
	def dataProvider
	
	//TODO:
	String dataSourceName
	
	String tableName
	
	Class modelClass
	
	List<String> allDimensions
	
	Map<String,String> dimensionToProperty
	
	def nameStrategy
	
	String valueDimension='value'
	
	String dimensionAtDataField
	
	Map<String,String> dataFieldsMap
	
	List dataModels
	
	String evalTableName(params,runtimeContext,sql){
		getTableName()
	}
	
	def evaluateRowModel(def rowModel,List<String> dataDimensions){
		
		if(rowModel==null){
			return null
		}
		
		def vd=getValueDimension()
		if(rowModel.containsKey(vd)){
			return rowModel
		}
		
		def dadf=getDimensionAtDataField()
		def dfm=getDataFieldsMap()
		if(dadf && dfm){
			//flatten ...
			def models=[]
			dfm.each{headValue,fieldName->
				if(!rowModel.containsKey(fieldName)){
					return
				}
				def flatModel=rowModel.clone()
				flatModel[dadf]=headValue
				def value=flatModel.remove(fieldName)
				flatModel[vd]=value
				models << flatModel
			}
			
			return models
		}
		
		return rowModel
	}
}

