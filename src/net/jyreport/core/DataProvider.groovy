package net.jyreport.core

/**
 *
 */
interface DataProvider {
	
	def initialize()
	
	TableDataModel selectData()

    void close()
}

