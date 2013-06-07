package net.jyreport.core.dsl

import net.jyreport.core.*

/**
 *
 */
class BaseBuilder extends BuilderSupport {
	
	def nameRegistrar
	
	Map<String,String> getNameMapping(){
		[:]
	}
	
	protected Object getName(String methodName) {
		def nm=getNameMapping()
		if(nm==null){
			return methodName
		}
		nm[methodName] ?: methodName
	}
	
	protected parseDate(ds){
		if(ds ==~ /\d+/){
			return Date.parse('yyyyMMdd',ds)
		}else if(ds ==~ /\d+-\d+-\d+/){
			return Date.parse('yyyy-M-d',ds)
		}
	}
	
	protected void setParent(Object parent, Object child){
	}
	
	protected Object createNode(Object name){
		createNode(name,null,null)
	}
	
	protected Object createNode(Object name, Object value){
		createNode(name,null,value)
	}
	
	protected Object createNode(Object name, Map attributes){
		createNode(name,attributes,null)
	}
	
	protected Object createNode(Object name, Map attributes, Object value){
		if(value instanceof ClosureWrapper){
			value=value?.closure
		}
		doCreateNode(name,attributes,value)
	}
	
	protected void nodeCompleted(Object parent, Object node) {
	}
	
	
	protected boolean setProperty(Object target,Object name, Object value, Map attributes){
		if(target==null){
			return false
		}
		if(value instanceof ClosureWrapper){
			value=value?.closure
		}
		boolean hasProperty=target.hasProperty(name)
		if(hasProperty){
			target[name]=value
		}else{
			logUnknownName(name,value)
		}
		if(attributes!=null){
			setProperties(target,attributes)
		}
		return hasProperty
	}
	
	protected void setProperties(Object target,Map attributes){
		if(target==null||attributes==null){
			return
		}
		attributes.each{name,value->
			if(target.hasProperty(name)){
				target[name]=value
			}else{
				logUnknownName(name,value)
			}
		}
	}
	
	protected parseDataType(value,attributes){
		def dataType
		if(value instanceof DataType){
			dataType=value
		}else if(value instanceof Class){
			dataType=value.newInstance()
		}else if(value instanceof String){
			def registrarClass=nameRegistrar?.dataTypeClasses[value]
			if(registrarClass){
				dataType=registrarClass.newInstance()
			}
		}else if(value!=null){
			logUnknownValue('dataType',value)
			return null
		}
		if(dataType==null){
			log("dataType is null!")
			return null
		}
		if(attributes!=null){
			setProperties(dataType,attributes)
		}
		return dataType
	}
	
	protected parseDataTable(value,attributes){
		
		def dataTable
		if(value instanceof DataTable){
			dataTable=value
		}else if(value instanceof Class){
			dataTable=value.newInstance()
		}else if(value instanceof String){
			def registrarClass=nameRegistrar?.dataTableClasses[value]
			if(registrarClass){
				dataTable=registrarClass.newInstance()
			}
		}else if(value!=null){
			logUnknownValue('dataTable',value)
			return null
		}
		if(attributes!=null){
			if(dataTable==null){
				dataTable=new DataTable()
			}
			setProperties(dataTable,attributes)
		}
		return dataTable
	}
	
	protected parseNameStrategy(value,attributes){
		def nameStrategy
		if(value instanceof NameStrategy){
			nameStrategy=value
		}else if(value instanceof Class){
			nameStrategy=value.newInstance()
		}else if(value==null){
			nameStrategy=new NameStrategy()
		}else{
			logUnknownValue('nameStrategy',value)
			return null
		}
		if(attributes!=null){
			setProperties(nameStrategy,attributes)
		}
		return nameStrategy
	}
	
	protected tn(){
		if(current instanceof String){
			return current
		}
		(current ?: this)?.class.simpleName
	}
	
	protected log(message){
		println "[${tn()}] $message"
	}
	
	protected logUnknownName(name,value){
		println "[${tn()}] unknown name: $name"
	}
	
	protected logUnknownValue(name,value){
		println "[${tn()}] $name unknown value"
	}
	
	protected logSnha(name,attributes){
		println "[${tn()}] $name should not have attributes"
	}
}
