
code 'cf_mcf_reprice_structure'
name '重定价结构'
dimensions {
	accbook {
		subDimension 'repriceFreq'
		appendSubHeadCallback { thisHead,subHead->
			thisHead.model?.reprice
		}
	}
	monthEnd name:'cfDate'
}
dataRequest {
	table 'mcfd',{
		allDimensions (['organ','currency','accbook','repriceFreq','cfDate'])
	}
	fields 'sum(endingBalance) value'
	groupFields 'accbook,repriceFreq,cfDate'
	contextParams (['date','organ','currency','staticModel'])
}
//heads {
//	accbook {
//		params {
//			reprice true
//		}
//	}
//}
dataGrids {
	apply selectRows({children!=null}),{
		formula {sumChildren()}
	}
	apply selectRows({children==null && dimension.name=='accbook'}),{
		params {repriceFreq null}
	}
	apply selectRows({dimension.name=='repriceFreq'}),{
		evaluatedCallback SET_PERCENT_TO_PARENT
	}
}
