package net.jyreport.core.dsl

import net.jyreport.core.*

/**
 *
 */
class DataTableBuilder extends BaseBuilder {
	
	DataTable dataTable
	
	DataTableBuilder(dataTable){
		this.dataTable=dataTable
		setCurrent(dataTable)
	}

    protected void setClosureDelegate(Closure closure, Object node) {
		def del
		if(node instanceof NameStrategy){
			del=new NameStrategyBuilder(node)
		}else{
			del=this
		}
		del.nameRegistrar=this.nameRegistrar
		closure.resolveStrategy=Closure.DELEGATE_FIRST
		closure.setDelegate(del)
    }
	
    protected Object doCreateNode(Object name, Map attributes, Object value){
		
		if(name=='nameStrategy'){
			def nameStrategy=parseNameStrategy(value,attributes)
			if(nameStrategy==null){
				return null
			}
			dataTable.nameStrategy=nameStrategy
			return nameStrategy
		}
		
		if(name=='dataFieldsMap'){
			dataTable.dataFieldsMap=attributes
			return
		}
		
		if(name=='dimensionToProperty'){
			dataTable.dimensionToProperty=attributes
			return
		}
		
		setProperty(dataTable,name,value,attributes)
		return name
    }
}
