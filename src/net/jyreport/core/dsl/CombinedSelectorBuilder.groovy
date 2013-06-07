package net.jyreport.core.dsl

import net.jyreport.core.selector.*
import net.jyreport.core.*

import static net.jyreport.core.Selector.*

/**
 *
 */
class CombinedSelectorBuilder extends BaseBuilder {
	
	//and/or/not
	String combineMode
	
	protected def combineBuilder
	
	protected List selectors
	
	CombinedSelectorBuilder(combineMode){
		this.combineMode=combineMode
		selectors=[]
	}

	protected void setClosureDelegate(Closure closure, Object node) {
		def del
		if(node==combineBuilder){
			del=combineBuilder
		}else if(node instanceof Selector){
			del=new SelectorBuilder(node)
			del.nameRegistrar=this.nameRegistrar
		}else{
			del=this
		}
		closure.resolveStrategy=Closure.DELEGATE_FIRST
		closure.setDelegate(del)
	}
	
	protected Object doCreateNode(Object name, Map attributes, Object value){
		def node=name
		switch(name){
			case 'select':
			def selector
			if(value==null && attributes!=null){
				selector=buildSelector(attributes)
			}else{
				selector=buildSelector(value)
				setProperties(selector,attributes)
			}
			selectors << selector
			node=selector
			break
			case ['and','or','not']:
			if(name=='not' && value!=null){
				def selector
				if(value==null && attributes!=null){
					selector=buildSelector(attributes)
				}else{
					selector=buildSelector(value)
					setProperties(selector,attributes)
				}
				node=selector
				selectors << new NotSelector(selector)
				break
			}
			if(value!=null){
				logUnknownValue(name,value)
			}
			if(attributes!=null){
				logSnha name,attributes
			}
			combineBuilder=new CombinedSelectorBuilder(name)
			node=combineBuilder
			break
			default:
			logUnknownName(name,value)
		}
		node
	}
	
	protected void nodeCompleted(Object parent, Object node) {
		if(node==combineBuilder){
			selectors << combineBuilder.result
		}
	}
	
	Selector getResult() {
		if(selectors?.size()==0){
			log "no selector!"
		}
		def selector=selectors.head()
		def rest=selectors.tail()
		switch(combineMode){
			case 'and':
			rest.each{selector=selector.and(it)}
			break
			case 'or':
			case 'not':
			rest.each{selector=selector.or(it)}
			if(combineMode=='not'){
				selector=selector.reverse()
			}
			break
		}
		return selector
	}
}
