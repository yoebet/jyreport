package net.jyreport.core.dataprovider

import groovy.sql.Sql
import net.jyreport.core.*
import net.jyreport.core.grid.*
import static net.jyreport.core.support.SqlUtil.*

/**
 *
 */
class SqlDataProvider extends AbstractDataProvider {
	
	Sql sql
	
	protected nameStrategy
	
	//private sumFieldPattern= ~/^\s*sum\((\w+)\) +(\w+)\s*$/
	
	def initialize(){
		super.initialize()
        if(sql==null && config.dataSource){
		    sql=new Sql(config.dataSource)
        }
	}
	
	protected parseCustomQuery(query,params){
		
		DataTable dataTable=dataRequest.dataTable
		def tableName=dataTable.evalTableName(params,runtimeContext,sql)
		def expandMap=[
			TABLE: tableName,
			FIELDS: dataRequest.fields,
			GROUP_FIELDS: dataRequest.groupFields,
			REPORT_CODE:report.code]
		
		def paramList=[]
		
		query=query.replaceAll(~/#(\w+)#/,{
				expandMap[it[1]]
			}).replaceAll(~/\:(\w+)/,{
				paramList << params[it[1]]
				'?'
			})
		
		return [query,paramList]
	}
	
	//return [sqlString,paramList]
	protected List buildQuery(params){
		
		DataTable dataTable=dataRequest.dataTable
		def dimensionToProperty=dataTable.dimensionToProperty ?: [:]
		
		def tableName=dataTable.evalTableName(params,runtimeContext,sql)
		List<List> orders=dataRequest.orders
		
		def toFieldName=nameStrategy.propertyToField
		
		dataRequest.headValuesMap?.each{ dimensionName,headValues ->
			params[dimensionName]=['in',headValues]
		}
		
		def dimOrPropToField= { dimensionName ->
			def propertyName=dimensionToProperty[dimensionName] ?: dimensionName
			toFieldName(propertyName)
		}
		
		def groupFields = dataRequest.groupFields
		def fields = dataRequest.fields
		if(fields){
			def fieldList = fields.split(',').toList()
			if(groupFields){
				def groupFieldList = groupFields.split(',').toList()
				def implicitFields=groupFieldList-fieldList
				if(implicitFields){
					fieldList=implicitFields+fieldList
				}
			}
			fields=fieldList.collect{
				dimOrPropToField(it)
			}.join(',')
		}
		
		def sqlString='select ' << (fields ?: '*') << '\n'
		sqlString << ' from ' << tableName << '\n'
        
		def paramList=[]
		def conditions=processParams(params,paramList,dimOrPropToField)
		def orderString=processOrders(orders,dimOrPropToField)
		
		for(int i=paramList.size()-1;i>0;i--){
			if(paramList[i] instanceof Date){
				paramList[i]=new java.sql.Date(paramList[i].time)
			}
		}
		
		if(conditions){
			sqlString << conditions << '\n'
		}
		if(groupFields){
			groupFields = groupFields.split(',').collect{dimOrPropToField(it)}.join(',')
			sqlString << ' group by '+groupFields << '\n'
			def groupHaving=dataRequest.groupHaving
			if(groupHaving){
				groupHaving = groupHaving.split(/\b/).collect{dimOrPropToField(it)}.join('')
				sqlString << ' having ' << groupHaving << '\n'
			}
		}
		if(orderString){
			sqlString << orderString << '\n'
		}
		
		return [sqlString.toString(),paramList]
	}
	
	protected doSelect(sqlString,paramList,dataDimensions){
		
		def dataTable=dataRequest.dataTable
		def dimensionToProperty=dataTable.dimensionToProperty ?: [:]
		Map<String,String> propertyToDimension=dimensionToProperty.collectEntries { key, value -> [value, key] }
		
		Pagination pagination=dataRequest.pagination
		if(pagination==null){
			//pagination=new Pagination(perPage: 3000)
		}
		
		def fieldToPropertyName=nameStrategy.fieldToProperty
		def fieldToDimOrProperty=[:]
		
		List<Object> models=[]
		
		int dbRowCount=0
		
		def rowHander={ row ->
			def rr=row.toRowResult()
			def model=rr.collectEntries { fieldName,value ->
				def dop=fieldToDimOrProperty[fieldName]
				if(dop==null){
					def propertyName=fieldToPropertyName(fieldName)
					dop=propertyToDimension[propertyName] ?: propertyName
					fieldToDimOrProperty[fieldName]=dop
				}
				[dop,value]
			}
			dbRowCount++
			
			def result=dataTable.evaluateRowModel(model,dataDimensions)
			if(result==null){
				return
			}
			if(result instanceof List){
				models.addAll(result)
			}else{
				models << result
			}
		}
        println "${sqlString} -> ${paramList}"
		if(pagination==null){
			sql.eachRow(sqlString, paramList, rowHander)
		}else{
			sql.eachRow(sqlString, paramList, pagination.offset, pagination.perPage, rowHander)
		}
		println "db rows: $dbRowCount, models: ${models.size()}"
		
		return models
	}
	
	TableDataModel selectData(){
		
		DataTable dataTable=dataRequest.dataTable
		
		if(dataTable.dataModels){
			def tableDataModel=fromStaticDataModels(dataTable.dataModels)
			if(tableDataModel){
				return tableDataModel
			}
		}
		
		nameStrategy=dataTable.nameStrategy ?: config.nameStrategy
		if(nameStrategy instanceof Class){
			nameStrategy=nameStrategy.newInstance()
		}
		
		if(dataRequest.params==null){
			dataRequest.params=[:]
		}
		Map<String,Object> params=dataRequest.params.clone()

		appendParams(params)
		
		def queryAndParams
		if(dataRequest.query){
			queryAndParams=parseCustomQuery(dataRequest.query,params)
		}else{
			queryAndParams=buildQuery(params)
		}
		def sqlString=queryAndParams[0]
		def paramList=queryAndParams[1]
		transParam(paramList)
		
		def allDimensions=dataTable.allDimensions
        println "all_dimensions: $allDimensions"
		Map<String,Object> implitParams=dataRequest.params.findAll{
			!(it.value instanceof List) && allDimensions.contains(it.key)
		}
		List<String> dataDimensions=allDimensions-implitParams.keySet()
        println "data_dimensions: $dataDimensions"
		
		List<Object> models=doSelect(sqlString,paramList,dataDimensions)
		
		def tdm=evalTableDataModel(dataRequest)
		tdm.dataRequest=dataRequest
		tdm.implitParams=implitParams
		tdm.dimensions=dataDimensions
		tdm.dataModels=models
		return tdm
	}
	
	void close(){
		sql?.close()
	}
	
}

