package net.jyreport.core

/**
 *
 */
class Pagination implements Serializable,Cloneable {
	
	int perPage=300
	
	Integer total
	
	// 1 based
	Integer offset
	
	Integer pages
	
	// 1 based
	int page=1
	
	//boolean reverse
	
	Integer getOffset(){
		if(offset==null){
			offset=perPage * (page-1) + 1
		}
		return offset
	}
	
	Integer getPages(){
		if(pages==null){
			if(total==null){
				return null
			}
			pages=total/perPage
			if(total%perPage!=0){
				pages++
			}
		}
		return pages
	}
	
	String toString(){
		"perPage: $perPage, page: $page"
	}
}

