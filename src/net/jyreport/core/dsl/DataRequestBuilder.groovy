package net.jyreport.core.dsl

import net.jyreport.core.*

/**
 *
 */
class DataRequestBuilder extends DataCriteriaBuilder {
	
	def dataRequest
	
	Map<String,String> getNameMapping(){
		(super.getNameMapping() ?: [:]) + [table:'dataTable']
	}
	
	DataRequestBuilder(dataRequest){
		super(dataRequest)
		this.dataRequest=dataRequest
	}

    protected void setClosureDelegate(Closure closure, Object node) {
		def del
		if(node instanceof DataTable){
			del=new DataTableBuilder(node)
		}else{
			del=this
		}
		del.nameRegistrar=this.nameRegistrar
		closure.resolveStrategy=Closure.DELEGATE_FIRST
		closure.setDelegate(del)
    }
	
    protected Object doCreateNode(Object name, Map attributes, Object value){
		if(name=='name'){
			if(value instanceof String){
				dataRequest.name=value
			}else{
				logUnknownValue(name,value)
			}
			return null
		}
		if(name=='dataTable'){
			dataRequest.dataTable=parseDataTable(value,attributes)
			if(dataRequest.dataTable==null){
				dataRequest.dataTable=new DataTable()
			}
			return dataRequest.dataTable
		}
		if(name=='headValuesMap'){
			if(attributes!=null){
				dataRequest.headValuesMap=attributes
			}else if(value instanceof Map){
				dataRequest.headValuesMap=value
			}
			if(dataRequest.headValuesMap==null){
				dataRequest.headValuesMap=[:]
			}
			return 'headValuesMap'
		}
		if(current=='headValuesMap'){
			//name: dimension
			dataRequest.headValuesMap[name]=(value as List)
			return 'headValues'
		}
		
		return super.doCreateNode(name,attributes,value)
    }
}
