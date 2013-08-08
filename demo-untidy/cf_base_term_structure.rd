
code 'cf_base_term_structure'
name '基期期限结构'
dimensions {
	accbookWithTerm null
	dummy {
		dummy true
		headModels ([[id:1,name:'基期']])
	}
}
dataRequest {
	table 'baseAccOrg',{
		allDimensions (['organ','currency','accbook','term'])
	}
	fields 'sum(balance) value'
	groupFields 'accbook,term'
	contextParams (['date','organ','currency','staticModel'])
}
heads {
	accbook {
		params {
			hasTerm true
		}
	}
}
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