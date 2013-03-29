package net.jyreport.core.selector

import net.jyreport.core.grid.DataGrid
import net.jyreport.core.*

/**
 *
 */
abstract class CombinedSelector extends Selector {
	
	Selector selector1
	
	Selector selector2
	
	CombinedSelector(s1,s2){
		selector1=toSelector(s1)
		selector2=toSelector(s2)
		reportData=selector1.reportData ?: selector2.reportData
	}
	
	void setReportData(ReportData rd){
		super.setReportData(rd)
		selector1.setReportData(rd)
		selector2.setReportData(rd)
	}
}
