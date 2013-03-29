package net.jyreport.core

/**
 *
 */
interface ResultCache {
	
	def initialize()
	
	def fetch(String type,String reportCode,Map context)
	
	boolean cache(String type,String reportCode,Map context,def result)
	
	boolean drop(String type,String reportCode,Map context)
	
}
