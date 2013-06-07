package net.jyreport.core

/**
 *
 */
class DataCriteria implements Serializable,Cloneable {
	
	//定制查询sql。如果设置了它，直接使用它来查询数据，fields、groupFields不会使用
	def query
	
	String fields
	
	String groupFields
	
	String groupHaving
	
	Map<String,Object> params
	
	Pagination pagination
	
	List<List> orders
	
	void setPager(pag){
		if(pag instanceof Map){
			setPagination(new Pagination(pag))
		}else{
			setPagination(pag)
		}
	}
	
	Object clone(){
		def c=super.clone()
		c.params=getParams()?.clone()
		c.pagination=getPagination()?.clone()
		c.orders=getOrders()?.clone()
		c
	}
	
}

