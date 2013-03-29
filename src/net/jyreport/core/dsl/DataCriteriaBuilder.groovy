package net.jyreport.core.dsl

import net.jyreport.core.*

/**
 *
 */
class DataCriteriaBuilder extends BaseBuilder {
	
	def dataCriteria
	
	DataCriteriaBuilder(dataCriteria){
		this.dataCriteria=dataCriteria
		setCurrent(dataCriteria)
	}
	
	Map<String,String> getNameMapping(){
		[pager:'paginate']
	}
	
    protected Object doCreateNode(Object name, Map attributes, Object value){
		def node=name
		switch(current){
			case null:
			if(name=='call'){
				if(dataCriteria==null){
					dataCriteria=new DataCriteria()
				}
			}
			node=dataCriteria
			break
			case dataCriteria:
			switch(name){
				case 'paginate':
				if(value instanceof Pagination){
					dataCriteria.pagination=value
				}else if(value!=null){
					logUnknownValue(name,value)
				}
				if(dataCriteria.pagination==null){
					dataCriteria.pagination=new Pagination()
				}
				if(attributes!=null){
					setProperties(dataCriteria.pagination,attributes)
				}
				break
				case 'contextParams':
				if(dataCriteria.params==null){
					dataCriteria.params=[:]
				}
				if(value instanceof String){
					dataCriteria.params[value]='context[]'
				}else if(value instanceof List){
					value.each{
						dataCriteria.params[it]='context[]'
					}
				}else{
					logUnknownValue(name,value)
				}
				break
				case 'params':
				if(attributes!=null){
					dataCriteria.params=attributes
				}else if(value instanceof Map){
					dataCriteria.params=value
				}else if(value!=null){
					logUnknownValue(name,value)
				}
				if(dataCriteria.params==null){
					dataCriteria.params=[:]
				}
				break
				case 'orders':
				def orders
				if(value instanceof List){
					dataCriteria.orders=value
				}else if(value instanceof String){
					dataCriteria.orders=value.split(',').collect{
						it.split(' ') as List
					}
				}else{
					logUnknownValue(name,value)
				}
				break
				case 'order':
				if(dataCriteria.orders==null){
					dataCriteria.orders=[]
				}
				if(value instanceof List){
					dataCriteria.orders << value
				}else if(value instanceof String){
					dataCriteria.orders << (value.split(' ') as List)
				}
				break
				default:
				setProperty(dataCriteria,name,value,attributes)
			}
			break
			case 'paginate':
			setProperty(dataCriteria.pagination,name,value,null)
			break
			case 'orders':
			if(name!='order'){
				logUnknownName(name,value)
			}
			if(value instanceof List){
				dataCriteria.orders << value
			}else if(value instanceof String){
				dataCriteria.orders << (value.split(' ') as List)
			}
			break
			case 'params':
			dataCriteria.params[name]=value
			break
			default:
			logUnknownName(name,value)
		}
		node
    }
}
