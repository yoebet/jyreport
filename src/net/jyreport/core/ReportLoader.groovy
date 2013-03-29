package net.jyreport.core

/**
 *
 */
interface ReportLoader {
	
	def initialize()
	
	Report loadReport(String reportCode)
}

