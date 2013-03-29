package net.jyreport.core.dsl

import net.jyreport.core.selector.*
import net.jyreport.core.*

import static net.jyreport.core.Selector.*

/**
 *
 */
class SelectorBuilder extends BaseBuilder {
	
	Selector selector
	
	SelectorBuilder(selector){
		this.selector=selector
		setCurrent(selector)
	}
	
	void condition(Closure condition){
		if(current.hasProperty('condition')){
			current.condition=condition
		}else{
			logUnknownName('condition',condition)
		}
	}
	
    protected Object doCreateNode(Object name, Map attributes, Object value){
		if(current instanceof BasicSelector){
			if(name=='location' && value==null && attributes!=null){
				current.location=attributes
				return name
			}
		}
		setProperty(current,name,value,attributes)
    }
}
