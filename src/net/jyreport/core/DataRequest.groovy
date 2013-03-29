package net.jyreport.core

/**
 *
 */
class DataRequest extends DataCriteria {
	
	DataTable dataTable
	
	Map<String,List<Object>> headValuesMap
	
	String name
	
	//class
	def tableDataModel
	
	Integer hitCount=0
	
	String getName(){
		if(name==null){
			name=getDataTable()?.name
		}
		return name
	}
	
	boolean match(Map<String,Object> params){
		def thisParams=this.params
		if(thisParams==null){
			return true
		}
		params.every{ k,v ->
			!thisParams.containsKey(k) || thisParams[k]==v
		}
	}
}

