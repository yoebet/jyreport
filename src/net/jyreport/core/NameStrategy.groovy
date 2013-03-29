package net.jyreport.core

/**
 *
 */
class NameStrategy {
	
	def propertyToField={ propertyName ->
		propertyName.replaceAll('[A-Z]',{'_'+it[0].toLowerCase()})
	}
	
	def fieldToProperty={ fieldName ->
		fieldName.toLowerCase().replaceAll('_(.)',{it[1].toUpperCase()})
	}
}

