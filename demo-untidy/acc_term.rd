
code 'MCF'
name '月现金流表'
dimensions {
	accbookWithTerm null
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
	accbookWithTerm {
		params {
			term_set_id (['not null'])
		}
		pager {
			perPage 3
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