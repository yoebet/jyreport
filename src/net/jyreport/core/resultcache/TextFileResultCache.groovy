package net.jyreport.core.resultcache

import net.jyreport.core.*

/**
 *
 */
class TextFileResultCache extends FileResultCache {
	
	def supportedTypes=['txt','json','html']
	
	def charset='UTF8'
	
	
	def fetch(String type,String reportCode,Map context){
		def cacheFile=getCacheFile(type,reportCode,context)
		if(!cacheFile){
			return null
		}
		
		return cacheFile.getText(charset)
	}
	
	boolean cache(String type,String reportCode,Map context,def result){
		if(!supportedTypes.contains(type)){
			println "unknown result type: ${type}"
			return false
		}
		
		def cacheFile=prepareCacheFile(type,reportCode,context)
		if(!cacheFile){
			return false
		}
		
		cacheFile.write(result.toString(),charset)
		return true
	}
	
	boolean drop(String type,String reportCode,Map context){
		
		def cacheFile=getCacheFile(type,reportCode,context)
		if(cacheFile){
			cacheFile.delete()
		}
		return true
	}
	
}

