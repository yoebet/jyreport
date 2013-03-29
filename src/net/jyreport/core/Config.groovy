package net.jyreport.core

import net.jyreport.core.headprocessor.*
import net.jyreport.core.dataprovider.*
import net.jyreport.core.reportloader.*
import net.jyreport.core.resultcache.*
import net.jyreport.core.resultbuilder.*
import net.jyreport.core.datatype.*
import net.jyreport.core.grid.*

//dataSource {}

reportDefaults {
	dataType=CurrencyType
	display=Display
}

headProcessor=SqlHeadProcessor

dataProvider=SqlDataProvider

nameStrategy=NameStrategy

reportProcessor=ReportProcessor

formulaCategory=FormulaCategory

tableDataModel=TableDataModel

reportLoaders {
	dslFile {
		clazz=DSLReportLoader
		properties {
			fileBaseDir="reports"
			filePostfix=".rd"
			dslWrapper={reportCode->'_wrapper'}
		}
	}
//	db {
//	}
}

reportLoaderEvaluator={reportCode->
	'dslFile'
}

resultBuilders {
	txt {
		clazz=TextResultBuilder
		properties {
		}
	}
	json {
		clazz=JsonResultBuilder
		properties {
		}
	}
}

resultCaches {
	txt {
		clazz=TextFileResultCache
		properties {
			cacheBaseDir="report_result"
			cacheNameGenerator={type,reportCode,context->
				"${reportCode}.${type}"
			}
		}
	}
//	xls {
//	}
}

resultCacheEvaluator={type,reportCode,context->
	'txt'
}

resultCacheEnabled=false
