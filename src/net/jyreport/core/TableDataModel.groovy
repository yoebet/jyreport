package net.jyreport.core

/**
 *
 */
class TableDataModel implements Cloneable {
	
	DataRequest dataRequest
	
	Map<String,Object> implitParams
	
	List<String> dimensions
	
	String KEY_JOIN_BY='|'
	
	String AGGREGATED_PLACE_HOLDER='*'
	
	protected List<Object> dataModels
	
	protected Map<String,Object> dimensionsKeyToValue
	
	//010100|*|20120131 -> [1000.0,2000.0,...]
	protected Map<String,List<Object>> partialDimensionsMap
	
	//以生成的部分参数取值列表（在partialDimensionsMap中），['organ|*|date',...]
	protected Set<String> partialDimensionsKeys
	
	
	//rowModel: Map<String,Object> or Model
	void setDataModels(List<Object> dataModels){
		this.dataModels=dataModels
		DataTable dataTable=dataRequest.dataTable
		def valueDimension=dataTable.valueDimension
		dimensionsKeyToValue=dataModels.collectEntries { rowModel ->
			def dimensionValues=dimensions.collect{rowModel[it]}
			def key=genValuesKey(dimensionValues)
			def value=rowModel[valueDimension]
			//println "$key -> $value"
			[key,value]
		}
	}
	
	protected genValuesKey(dimensionValues){
		dimensionValues.eachWithIndex{value,index->
			if(value instanceof Date){
				dimensionValues[index]=value.format('yyyyMMdd')
			}
		}
		dimensionValues.join(KEY_JOIN_BY)
	}
	
	protected ensurePartialDimensionMap(partialDimensions){
		
		if(partialDimensionsMap==null){
			partialDimensionsMap=[:].withDefault{[]}
			partialDimensionsKeys=[]
		}
		def dimensionsKey=dimensions.collect{
			partialDimensions.contains(it)? it : AGGREGATED_PLACE_HOLDER
		}.join(KEY_JOIN_BY)
		
		if(partialDimensionsKeys.contains(dimensionsKey)){
			return
		}
		
		dataModels.each{ rowModel ->
			def dimensionValues=dimensions.collect{
				partialDimensions.contains(it)? rowModel[it] : AGGREGATED_PLACE_HOLDER
			}
			def key=genValuesKey(dimensionValues)
			partialDimensionsMap[key] << rowModel
		}
		partialDimensionsKeys << dimensionsKey
	}
	
	def getValue(Map<String,Object> params, Closure aggregator=null){
		
		//println "get: $params"
		
		params-=implitParams
        params.remove('dataRequest')
		
		if(params.size()==dimensions.size()){
			def dimensionValues=dimensions.collect{params[it]}
			def key=genValuesKey(dimensionValues)
			return dimensionsKeyToValue[key]
		}
		
		if(aggregator==null){
			return null
		}
		
		ensurePartialDimensionMap(params.keySet())
		
		def dimensionValues=dimensions.collect{
			params.containsKey(it)? params[it] : AGGREGATED_PLACE_HOLDER
		}
		def key=genValuesKey(dimensionValues)
		def models=partialDimensionsMap[key]
		
		return aggregator.call(models)
	}
	
	boolean match(Map<String,Object> params){
		if(implitParams==null){
			return true
		}
		params.every{ k,v ->
			if(!implitParams.containsKey(k)){
				return true
			}
			def iv=implitParams[k]
			if(iv instanceof List){
				//TODO:
				return true
			}
			iv == v
		}
	}
}

