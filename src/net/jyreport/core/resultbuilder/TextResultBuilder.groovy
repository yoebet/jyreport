package net.jyreport.core.resultbuilder

import net.jyreport.core.*

/**
 * for console test
 */
class TextResultBuilder implements ResultBuilder {
	
	int rhWidth=30
	
	int colWidth=11
	
	Closure textGenerator
	
	def build(ReportData reportData){
		
		def result=new StringBuffer()
		
		def lineSep=System.properties["line.separator"]
		
		def rlayers=reportData.report.layout.rowDimensions.size()
		
		def nl=[]
		def curcols=reportData.highestColHeadGrids
		
		def zhcount={
			it.findAll(~/[－　：（）\u4e00-\u9fa5]/).size()
		}
		
		def printColHead
		printColHead={
			rlayers.times{
				result << ' '*(rhWidth-1)
				result <<  '|'
			}
			curcols.each{ ch->
				def cols=ch.cols?.size() ?: 1
				def text=ch.text ?: ''
				result << text.center(colWidth*cols-1-zhcount(text))
				result <<  '|'
				if(ch.lowerHeadGrids){
					nl+=ch.lowerHeadGrids
				}
			}
			result <<  lineSep
			curcols=nl
			nl=[]
			if(curcols){
				printColHead()
			}
		}
		printColHead()
		
		reportData.dataGrids.each{ rowGrids ->
			if(rowGrids.size()==0){
				return
			}
			def firstGrid=rowGrids.first()
			def rhs=[]
			def crh=firstGrid.rowHeadGrid
			while(crh){
				rhs << crh
				crh=crh.higherHeadGrid
			}
			rhs.reverseEach{
				if(it.row==firstGrid.row){
					def ht='	'*it.layer+it.text
					result <<  ht.padRight(rhWidth-1-zhcount(ht))
				}else{
					result <<  ' '*(rhWidth-1)
				}
				result <<  '|'
			}
			
			if(textGenerator!=null){
				textGenerator.resolveStrategy=Closure.DELEGATE_FIRST
			}
			rowGrids.each { grid ->
				def text
				if(textGenerator==null){
					text=grid.text ?: ''
				}else{
					textGenerator.delegate=grid
					text=textGenerator.call()
					text=text?.toString() ?: ''
				}
				result << text.padLeft(colWidth-1-zhcount(text))
				result <<  '|'
			}
			result << lineSep
		}
		
		return result
	}
}

