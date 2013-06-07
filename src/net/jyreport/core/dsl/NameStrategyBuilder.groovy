package net.jyreport.core.dsl

import net.jyreport.core.*

/**
 *
 */
class NameStrategyBuilder extends BaseBuilder {
	
	def nameStrategy
	
	NameStrategyBuilder(nameStrategy){
		this.nameStrategy=nameStrategy
		setCurrent(nameStrategy)
	}
	
	void propertyToField(Closure ptf){
		nameStrategy.propertyToField=ptf
	}
	
	void fieldToProperty(Closure ftp){
		nameStrategy.fieldToProperty=ftp
	}
	
	protected Object doCreateNode(Object name, Map attributes, Object value){
		logUnknownName(name,value)
		name
	}
}
