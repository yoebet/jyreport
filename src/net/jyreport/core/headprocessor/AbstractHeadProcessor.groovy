package net.jyreport.core.headprocessor

import net.jyreport.core.*

/**
 *
 */
abstract class AbstractHeadProcessor implements HeadProcessor {

	Dimension dimension

	DataCriteria headCriteria
	
	Map<String,Object> extraParams
	
	Report report

	def config
	
	def runtimeContext

	def initialize(){
	}

	protected void appendParams(params){
	}
	
	protected void setDimensionAttributes(){
		def context=report.context
		def dimensionParams=context["dimension.${dimension.name}"]
		dimensionParams?.each{name,value ->
			if(dimension.hasProperty(name)){
				try{
					dimension."${name}"=value
				}catch(e){
					println e
				}
			}else{
				println "dimension(${dimension.name}) does not have property: $name"
			}
		}
	}
	
	abstract List<Head> expand()

	void close(){
    }
	
}

