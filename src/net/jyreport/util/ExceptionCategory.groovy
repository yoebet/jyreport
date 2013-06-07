package net.jyreport.util

import org.codehaus.groovy.runtime.StringBufferWriter

class ExceptionCategory {
	
	static void printStackTrace2(Exception e) {
		printGroovyStackTrace(e)
	}
	
	static void printGroovyStackTrace(Exception e) {
		println groovyStackTraceAsString(e)
		//e.printStackTrace()
	}
	
	static String groovyStackTraceAsString(Exception e) {
		
		def lineSep=System.properties["line.separator"]
		def s= lineSep << e << lineSep
		e.stackTrace.each {
			def tr=it.toString()
			if( !tr.contains('.java') && tr =~ /:\d+\)$/ ){
				s << "\tat " << tr << lineSep
			}
		}
		s << lineSep
		s.toString()
	}

	static String stackTraceAsString(Exception e) {
		def s=new StringBuffer()
		e.printStackTrace(new PrintWriter(new StringBufferWriter(s)))
		s.toString()
	}
}
