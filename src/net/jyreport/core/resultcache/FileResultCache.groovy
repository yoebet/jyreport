package net.jyreport.core.resultcache

import net.jyreport.core.*

/**
 *
 */
abstract class FileResultCache implements ResultCache {
	
	def config
	
	def cacheBaseDir
	
	def cacheNameGenerator
	
	def initialize(){
        cacheBaseDir=cacheBaseDir ?: ''
		cacheBaseDir=cacheBaseDir.replace('\\','/')
		if(cacheBaseDir && !cacheBaseDir.endsWith('/')){
			cacheBaseDir+='/'
		}
	}
	
	protected getCacheFile(String type,String reportCode,Map context){
		
		def baseDirFile=new File(cacheBaseDir)
		if(!baseDirFile.exists()){
			return null
		}
		def resultCacheName=cacheNameGenerator.call(type,reportCode,context)
		if(!resultCacheName){
			return null
		}
		def fullName="${cacheBaseDir}${resultCacheName}"
		def cacheFile=new File(fullName)
		if(!cacheFile.exists()){
			return null
		}
		
		return cacheFile
	}
	
	protected prepareCacheFile(String type,String reportCode,Map context){
		
		def baseDirFile=new File(cacheBaseDir)
		if(!baseDirFile.exists()){
			baseDirFile.mkdirs()
		}
		
		def resultCacheName=cacheNameGenerator.call(type,reportCode,context)
		if(!resultCacheName){
			return null
		}
		def fullName="${cacheBaseDir}${resultCacheName}"
		def cacheFile=new File(fullName)
		def parentDirFile=cacheFile.parentFile
		if(!parentDirFile.exists()){
			parentDirFile.mkdirs()
		}
		if(cacheFile.exists()){
			cacheFile.delete()
		}
		return cacheFile
	}
	
	abstract def fetch(String type,String reportCode,Map context)
	
	abstract boolean cache(String type,String reportCode,Map context,def result)
	
	abstract boolean drop(String type,String reportCode,Map context)
	
}

