
code 'instr_filter_hbd'
name ''
dimensions {
	item {
		headsByData true
		headsSortByData true
	}
	bal null
}
layout {
	rows 'item'
	cols 'bal'
}
dataRequest {
	table 'gl'
	params {
		date parseDate('2012-5-31')
		bank '110101'
		currency '01'
	}
	order (['endingBalance','desc'])
	pager {
		perPage 10
		page 1
	}
}
derivedHeads {
	item {
		head SUM_HEAD()
	}
}

instrument new Instrument(){

	def bsel
	void gridsEvaluated(){
		reportData.filterRows {rowHeadGrid,rowDataGrids->
			//rowDataGrids[0].value > 5740000
			if(bsel==null){
				bsel=buildSelector([bal:'credit'])
				bsel.reportData=reportData
				bsel=new FixedSelector(bsel.select())
			}
			def sel=bsel.and(new FixedSelector(rowDataGrids))
			def grids=sel.select()
			return (grids.size()>0 && grids[0].value < 8880000)
		}
		//reportData.filterColumns {colHeadGrid,colDataGrids->
		//	println colDataGrids[0].value
		//	colDataGrids[0].value > 7000000
		//}
	}
}