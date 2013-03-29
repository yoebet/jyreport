package net.jyreport.core.selector

import net.jyreport.core.grid.DataGrid
import net.jyreport.core.*

/**
 *
 */
class BasicSelector extends Selector {
	
	def location
	
	Closure condition
	
	BasicSelector(location){
		this.location=location
	}
	
	BasicSelector(location, Closure condition){
		this.location=location
		this.condition=condition
	}
	
	List<DataGrid> select(){
		List<DataGrid> grids
		switch(location){
			case Selector:
			grids=location.select()
			break
			case Closure:
			def allSelector=new AllSelector(location)
			allSelector.reportData=reportData
			grids=allSelector.select()
			break
			case Map:
			grids=reportData.grids(location)
			break
			case List:
			if(location.size()==2){
				grids=reportData.grids(location[0],location[1])
			}else{
				return null
			}
			break
			default:
			println "location $location"
			return null
		}
		
		if(condition==null){
			return grids
		}
		condition.resolveStrategy=Closure.DELEGATE_FIRST
		return grids.grep{
			condition.delegate=it
			condition.call()
		}
	}
}

