package net.jyreport.core

/**
 *
 */
interface HeadProcessor {
	
	def initialize()
	
	List<Head> expand()

	void close()
}

