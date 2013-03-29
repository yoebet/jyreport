package net.jyreport.core

/**
 *
 */
class DataType implements Serializable,Cloneable {
	
	String pattern
	
	String formatValue(def value,Display display){
		value?.toString()
	}
	
	def getName(){
		this.class.simpleName.replace('Type','').toLowerCase()
	}
}

