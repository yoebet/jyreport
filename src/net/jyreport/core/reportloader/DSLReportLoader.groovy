package net.jyreport.core.reportloader

import groovy.text.*

import net.jyreport.core.*

/**
 *
 */
class DSLReportLoader implements ReportLoader {
	
	def config
	
	def filePostfix
	
	def dslWrapper
	
	def charset='UTF8'
	
	Map<String,String> reportWrapperCache=[:]
	
	private def fileBaseDir
	
	def initialize(){
		fileBaseDir=fileBaseDir ?: ''
		fileBaseDir=fileBaseDir.replace('\\','/')
		if(fileBaseDir && !fileBaseDir.endsWith('/')){
			fileBaseDir+='/'
		}
	}
	
	Report loadReport(String reportCode){
		
		String scriptText
		def rdContent=getFileText("${fileBaseDir}${reportCode}${filePostfix ?: ''}")
		
		def rdWrapperName=dslWrapper? dslWrapper(reportCode) : '_wrapper'
		
		if(rdWrapperName){
			def rdWrapperText=reportWrapperCache[rdWrapperName]

			if(!rdWrapperText){
				rdWrapperText=getFileText("${fileBaseDir}${rdWrapperName}${filePostfix ?: ''}")
				reportWrapperCache[rdWrapperName]=rdWrapperText
			}
			scriptText=evalDefinitionText(rdWrapperText,rdContent)
		}else{
			scriptText=rdContent
		}

		def shell=new GroovyShell(this.class.classLoader)
		Report report=shell.evaluate(scriptText,reportCode)
		
		return report
	}
	
	private String getFileText(path){
		def rdContent
		if(path ==~ /^([A-Za-z]\:)?\/.*/){
			rdContent = new File(path).text
		}else{
			def is=DSLReportLoader.class.getResourceAsStream("/${path}")
			if(is!=null){
				rdContent=is.getText(charset)
			}else{
				rdContent=new File(path).getText(charset)
			}
		}
		return rdContent
	}
	
//	def textEngine=new SimpleTemplateEngine()
	
	protected String evalDefinitionText(rdWrapperText,content){
//		def template = textEngine.createTemplate(rdWrapperText)
//		template.make([context:context, content:content])
//		template.toString()
		
		rdWrapperText.replace('$content',content)
	}
}

