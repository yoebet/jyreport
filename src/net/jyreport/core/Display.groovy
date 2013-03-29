package net.jyreport.core

/**
 *
 */
class Display implements Serializable,Cloneable {
	
	def currencyUnit='万'
	
	String currencyPattern="#,##0"
	
	String numericPattern="#,##0.00"
	
	String integerPattern="#,##0"
	
	String percentPattern="0.00%"
	
	String datePattern='yyyy-M-d'
	
}

