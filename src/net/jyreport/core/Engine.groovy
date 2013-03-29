package net.jyreport.core

import net.jyreport.util.*
import net.jyreport.core.datasupport.*
import net.jyreport.core.dataprovider.*
import net.jyreport.core.reportloader.*
import net.jyreport.core.resultbuilder.*

/**
 *
 */
class Engine {
	
    def dataSource
	
	def config
	
	boolean resultCacheEnabled
	
	boolean resultCacheExpired
	
	protected Map<String,ReportLoader> reportLoadersMap
	
	protected Map<String,ResultBuilder> resultBuildersMap
	
	protected Map<String,ResultCache> resultCachesMap
	
	protected Closure reportLoaderEvaluator
	
	protected Closure resultCacheEvaluator
	
	protected Map<String,Report> reportCache=[:]
	
	
	//TODO: ReportDataCache, HeadsCache
    
    def init(){
		def defaultConfig=new ConfigSlurper().parse(Config)
		if(config==null){
			config=defaultConfig
		}else{
			config=defaultConfig.merge(config as ConfigObject)
		}
        if(dataSource){
			config.dataSource=dataSource
        }else{
			println "no dataSource!"
		}
		
		reportLoaderEvaluator=config.reportLoaderEvaluator
		resultCacheEvaluator=config.resultCacheEvaluator
		if(config.resultCacheEnabled instanceof Boolean){
			resultCacheEnabled=config.resultCacheEnabled
		}
		
		reportLoadersMap=buildComponentsMap(config.reportLoaders)
		resultBuildersMap=buildComponentsMap(config.resultBuilders)
		resultCachesMap=buildComponentsMap(config.resultCaches)
		
		config=config.asImmutable()
    }
	
	protected buildComponentsMap(componentsConfig){
		
		def componentsMap=[:]
		componentsConfig.each{ componentName,componentConfig->
			try{
				def componentClass=componentConfig.clazz
				def component=componentClass.newInstance()
				componentConfig.properties?.each{name,value->
					if(component.hasProperty(name)){
						try{
							component."${name}"=value
						}catch(e){
							println e
						}
					}
				}
				if(component instanceof ReportLoader
					|| component instanceof ResultCache){
					component.config=config
					component.initialize()
				}
				componentsMap[componentName]=component
			}catch(e){
				println e
			}
		}
		return componentsMap
	}
	
	ReportData buildReport(Report report, Map<String,Object> context){
		report.context=context
		def processor=config.reportProcessor.newInstance()
		processor.config=config
		processor.report=report
        use(ExceptionCategory){
            return processor.buildReport()
        }
	}
    
	def fetchResultFromCache(String type,String reportCode, Map<String,Object> context){
		
		if(resultCacheExpired || !resultCacheEnabled){
			return null
		}
		use(ExceptionCategory){
			try{
				def cacheName=resultCacheEvaluator(type,reportCode,context)
				def resultCache=resultCachesMap[cacheName]
				return resultCache.fetch(type,reportCode,context)
			}catch(e){
				e.printGroovyStackTrace()
			}
		}
	}
    
	def dropResultFromCache(String type,String reportCode, Map<String,Object> context){
		
		def cacheName=resultCacheEvaluator.call(type,reportCode,context)
		def resultCache=resultCachesMap[cacheName]
		resultCache.drop(type,reportCode,context)
	}
    
	protected cacheReportResult(String type,String reportCode, Map<String,Object> context, def result){
		
		use(ExceptionCategory){
			try{
				def cacheName=resultCacheEvaluator.call(type,reportCode,context)
				def resultCache=resultCachesMap[cacheName]
				resultCache.cache(type,reportCode,context,result)
			}catch(e){
				e.printGroovyStackTrace()
			}
		}
	}
    
	def buildReportResult(String type, ReportData reportData, Map<String,Object> context, rebuild=false){
		
		def resultBuilder=resultBuildersMap[type]
		def result=resultBuilder.build(reportData)
		if(resultCacheEnabled || rebuild){
			cacheReportResult(type,reportData.report.code,context,result)
		}
		return result
	}
    
	def buildReportResult(String type, Report report, Map<String,Object> context, rebuild=false){
		
		ReportData reportData=buildReport(report,context)
		return buildReportResult(type,reportData,context)
	}
    
	def buildReportResult(String type, String reportCode, Map<String,Object> context, rebuild=false){
		
		if(!rebuild){
			def result=fetchResultFromCache(type,reportCode,context)
			if(result){
				return result
			}
		}
		
		Report report=loadReport(reportCode)
		return buildReportResult(type,report,context)
	}
	
	protected setDefaultValue(Report report){
		def defaultValues=config.reportDefaults
		defaultValues?.each{name,value->
			if(report.hasProperty(name)){
				try{
					if(report."${name}"!=null){
						return
					}
					if(value instanceof Class){
						value=value.newInstance()
					}else if(value instanceof Cloneable){
						value=value.clone()
					}
					report."${name}"=value
				}catch(e){
					println e
				}
			}
		}
	}
	
	void clearReportCache(){
		reportCache.clear()
	}
	
	Report loadReport(String reportCode, boolean reload=false){
		
		if(reload){
			reportCache[reportCode]=null
		}
		def report=reportCache[reportCode]
		if(report){
			return report.clone()
		}
		
		def loaderName=reportLoaderEvaluator.call(reportCode)
		ReportLoader reportLoader=reportLoadersMap[loaderName]
		
		report=reportLoader.loadReport(reportCode)
		if(report.code!=reportCode){
			println "report(${reportCode}) code inconsistent!"
			report.code=reportCode
		}
		
		setDefaultValue(report)
		
		reportCache[reportCode]=report
		
		return report.clone()
	}
	
}

