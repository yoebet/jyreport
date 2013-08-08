
code 'instr_lastTerm'
name ''
dimensions {
	item null
	bal {}
	icd {
		dummy true
		staticHeads {
			head model:[id:1,name:'本期']
			head model:[id:2,name:'上期'],formula:{lastTerm()}
			head {
                model ([id:3,name:'比上期 %'])
				formula {increaseRate()}
				dataType 'percent'
			}
		}
	}
}
layout {
	rows 'item'
	columns (['bal','icd'])
}
dataRequest {
	table 'gl'
	params {
		date parseDate('2012-5-31')
		bank '110101'
		currency '01'
	}
}
discoverDR true
heads {
	item {
		params {
			code (['like','1%'])
		}
		paginate {
			perPage 10
			page 1
		}
	}
}
derivedHeads {
	item {
		head SUM_HEAD()
	}
}
dataGrids {
	apply sel([-1,[2,5,8]]), {
		formula {(left.left-left)/left}
	}
}

//instrument new Instrument(){
//	List<Head> headsExpanded(dimension,List<Head> heads,Head parentHead=null){
//		if(dimension.name=='bal'){
//			heads.grep{it.model.id!='credit'}
//		}
//	}
//	List<Head> lowerHeads(HeadGrid headGrid, List<Head> lowerHeads){
//		if(headGrid.head.model.id=='debit'){
//			def last=lowerHeads.last()
//			lowerHeads[-1]=new Head(name:'比上期',aggregate:true,formula:{left.left-left})
//			lowerHeads << last
//			lowerHeads
//		}
//	}
//}