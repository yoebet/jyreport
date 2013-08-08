
code 'cf_mcf_term_structure'
name '期限结构'
dimensions {
	accbookWithTerm null
	monthEnd name:'cfDate'
}
dataRequest {
	table 'mcfd',{
		allDimensions (['organ','currency','accbook','term','cfDate'])
	}
	fields 'sum(endingBalance) value'
	groupFields 'accbook,term,cfDate'
	contextParams (['date','organ','currency','staticModel'])
}
//heads {
//	accbook {
//		params {
//			hasTerm true
//		}
//	}
//}
dataGrids {
	apply selectRows({children!=null}),{
		formula {sumChildren()}
	}
	apply selectRows({children==null && dimension.name=='accbook'}),{
		params {term null}
	}
	apply selectRows({dimension.name=='term'}),{
		evaluatedCallback SET_PERCENT_TO_PARENT
	}
}