
code 'sub_dim_dyn'
name '动态子维度演示'
dimensions {
	accbook {
		subDimension 'term'
		subDimensionExpandParams { thisHead->
			def accitem=thisHead.model
			[termSetId: accitem.termSetId as Integer]
		}
	}
	monthEnd name:'cfDate'
}
dataRequest {
	table 'mcfd',allDimensions: ['organ','accbook','currency','cfDate',
		'accitemTerm']
	fields 'sum(endingBalance) value'
	groupFields 'accbook,cfDate,accitemTerm'
	contextParams (['date','organ','currency','staticModel'])
}
heads {
	accbook {
		params {
			term_set_id (['not null'])
		}
		pager {
			perPage 6
			page 1
		}
	}
	cfDate {
		pager {
			perPage 3
			page 2
		}
	}
}
derivedHeads {
	term ([SUM_HEAD()])
}