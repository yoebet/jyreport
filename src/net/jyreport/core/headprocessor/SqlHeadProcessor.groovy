package net.jyreport.core.headprocessor

import groovy.sql.Sql
import net.jyreport.core.*
import static net.jyreport.core.support.SqlUtil.*

/**
 *
 */
class SqlHeadProcessor extends AbstractHeadProcessor {

	Sql sql
	
	protected nameStrategy

	def initialize(){
		super.initialize()
        if(sql==null && config.dataSource){
		    sql=new Sql(config.dataSource)
        }
		nameStrategy=dimension.nameStrategy ?: config.nameStrategy
		if(nameStrategy instanceof Class){
			nameStrategy=nameStrategy.newInstance()
		}
	}
	
	protected parseCustomQuery(headCriteria,query,params){
		
		def expandMap=[
			TABLE: dimension.tableName(),
			FIELDS: headCriteria.fields ?: dimension.fields,
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
	
	protected List buildQuery(headCriteria,params){
		
		def tableName=dimension.tableName()
		List<List> orders=headCriteria.orders
		def toFieldName=nameStrategy.propertyToField
		def fields=headCriteria.fields ?: dimension.fields
		
		def  sqlString='select ' << (fields? toFieldName(fields) : '*') << '\n'
		sqlString << ' from ' << tableName << '\n'
		
		def paramList=[]
		def conditions=processParams(params,paramList,toFieldName)
		if(conditions){
			sqlString << conditions << '\n'
		}
		def orderString=null
		def orderProperty=dimension.orderProperty
		if(orderProperty){
			orderString=' order by ' << toFieldName(orderProperty)
		}else{
			orderString=processOrders(orders,toFieldName)
		}
		if(orderString){
			sqlString << orderString << '\n'
		}
		
		return [sqlString.toString(),paramList]
	}
	
	protected doSelect(headCriteria,sqlString,paramList){
		
		List<Object> models=[]
		
		Pagination pagination=headCriteria.pagination

		def toPropertyName=nameStrategy.fieldToProperty
		def fieldToProperty=[:]

		def rowHander={ row ->
			def rr=row.toRowResult()
			def model=rr.collectEntries { fieldName,value ->
				def propertyName=fieldToProperty[fieldName]
				if(propertyName==null){
					propertyName=toPropertyName(fieldName)
					fieldToProperty[fieldName]=propertyName
				}
				[propertyName,value]
			}

			models << model
		}

		println "$sqlString -> $paramList"
		//TODO: count if paginate
		if(pagination==null){
			sql.eachRow(sqlString, paramList, rowHander)
		}else{
			sql.eachRow(sqlString, paramList, pagination.offset, pagination.perPage, rowHander)
		}
		
		return models
	}
	
	protected fetchParentModels(models,modelsMap){
		
		def toFieldName=nameStrategy.propertyToField
		
		def idProperty=dimension.idProperty
		def parentIdProperty=dimension.parentIdProperty
		
		models.each{
			modelsMap[it[idProperty]]=it
		}
		
		def  missingParentIds=[] as SortedSet
		models.each{
			def parentId=it[parentIdProperty]
			if(parentId && modelsMap[parentId]==null){
				missingParentIds << parentId
			}
		}
		if(!missingParentIds){
			return []
		}
		
		def fields=headCriteria.fields ?: dimension.fields
		def tableName=dimension.tableName()
		def sqlString='select ' << (fields? toFieldName(fields) : '*')
		sqlString << ' from ' << tableName
		
		def paramList=[]
		def idField=toFieldName(idProperty)
		def nparams
		missingParentIds=missingParentIds as List
		if(missingParentIds.size()==1){
			nparams=[(idField): missingParentIds[0]]
		}else{
			nparams=[(idField): ['in',missingParentIds]]
		}
		sqlString << processParams(nparams,paramList,toFieldName)
		
		def orderProperty=dimension.orderProperty
		if(orderProperty){
			sqlString << ' order by ' << toFieldName(orderProperty)
		}
		
		def parentModels=doSelect(headCriteria,sqlString.toString(),paramList)
		if(parentModels){
			def ppModels=fetchParentModels(parentModels,modelsMap)
			if(ppModels){
				parentModels.addAll(ppModels)
			}
		}
		
		return parentModels
	}

	List<Head> expand(){
		
		setDimensionAttributes()
		
		List<Head> shs=dimension.staticHeads()
		if(shs){
			return shs
		}

		if(headCriteria==null){
			headCriteria=new DataCriteria()
		}
		Map<String,Object> params=headCriteria.params?.clone() ?: [:]
		if(extraParams){
			params+=extraParams
		}

		appendParams(params)
		
		def context=report.context
		
		def paramsCallback=dimension.paramsCallback
		if(paramsCallback!=null){
			paramsCallback.call(params,context,runtimeContext,sql)
		}

		def queryAndParams
		if(headCriteria.query){
			queryAndParams=parseCustomQuery(headCriteria,headCriteria.query,params)
		}else{
			queryAndParams=buildQuery(headCriteria,params)
		}
		def sqlString=queryAndParams[0]
		def paramList=queryAndParams[1]
		transParam(paramList)

		List<Object> models=doSelect(headCriteria,sqlString,paramList)
		
		Closure sorter=dimension.modelSorter
		if(sorter!=null){
			models=models.sort(sorter) as List
		}
		
		if(dimension.hierarchical && dimension.fetchParents){
			def nmodels=fetchParentModels(models,[:])
			if(!dimension.orderProperty && nmodels.size()>1){
				// sort nmodels(to keep models order)
				def idProperty=dimension.idProperty
				def parentIdProperty=dimension.parentIdProperty
				def nmap=nmodels.collectEntries{
					[it[idProperty],it]
				}
				def nms=[]
				models.each { model->
					def pmId=model[parentIdProperty]
					def pm=nmap[pmId]
					while(pm){
						nms << nmap.remove(pmId)
						pmId=pm[parentIdProperty]
						pm=nmap[pmId]
					}
					nms << model
				}
				models=nms
			}else{
				models.addAll(nmodels)
			}
		}
		
		List<Head> heads=dimension.wrapHeads(models)
		
		if(dimension.parentDimension && dimension.carryParentParam){
			heads.each{
				it.carryParentHeadParam=true
			}
		}
		
		return heads
	}

	void close(){
		sql?.close()
	}
}

