package net.jyreport.core.resultbuilder

import net.jyreport.core.*

/**
 *
 */
class HtmlResultBuilder implements ResultBuilder {
	
	String charset='UTF8'
	
	def build(ReportData reportData){
		
		def result=new StringBuffer()
		def report=reportData.report
		
		result << "<html>\n"
		result << "<head>\n"
		result << "<meta charset=\"${charset}\">\n"
		result << "<style>\n"
		result << """
	table {
		border-collapse:collapse;
	}
	th,td {
		padding: 2px;
		border: 1px solid gray;
	}
	td.data {
		text-align:right;
	}
"""
		result << "</style>\n"
		result << "</head>\n"
		
		result << "<body>\n"
		result << "<table>\n"
		result << "<caption><h3>${report.name}</h3></caption>"
		
		def rlayers=report.layout.rowDimensions.size()
		
		def nl=[]
		def curcols=reportData.highestColHeadGrids
		
		def printColHead
		printColHead={
			result <<  "\n<tr>\n"
			if(rlayers>1){
				result <<  """<th colspan="$rlayers"></th>"""
			}else{
				result <<  "<th></th>"
			}
			curcols.each{ ch->
				def cols=ch.cols?.size() ?: 1
				if(cols>1){
					result <<  """<th colspan="$cols">"""
				}else{
					result <<  "<th>"
				}
				result << ch.text ?: ''
				result <<  "</th>"
				if(ch.lowerHeadGrids){
					nl+=ch.lowerHeadGrids
				}
			}
			result <<  "\n</tr>\n"
			curcols=nl
			nl=[]
			if(curcols){
				printColHead()
			}
		}
		printColHead()
		
		
		reportData.dataGrids.each{ rowGrids ->
			result << "<tr>\n"
			def firstGrid=rowGrids.first()
			def rhs=[]
			def crh=firstGrid.rowHeadGrid
			while(crh){
				rhs << crh
				crh=crh.higherHeadGrid
			}
			rhs.reverseEach{
				if(it.row==firstGrid.row){
					def cs=it.lowerHeadGrids?.size()
					if(cs > 1){
						result <<  """<td rowspan="$cs">"""
					}else{
						result <<  '<td>'
					}
					result <<  '&nbsp;'*4*it.layer
					result <<  it.text
					result <<  '</td>'
				}
			}
			
			rowGrids.each { grid ->
				result <<  '<td class="data">'
				result <<  grid.text ?: ''
				result <<  '</td>'
			}
			result << "\n</tr>\n"
		}
		
		result << "</table>\n"
		
		result << "</body>\n"
		result << "</html>\n"
		
		return result
	}
}
