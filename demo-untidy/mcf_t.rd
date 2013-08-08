
code 'MCF'
name '月现金流表'
dimensions {
	accbook fetchParents:true
	monthEnd {
		name 'cfDate'
		maxMonths 60
		overview 'Q'
		align true
	}
}
layout {
	rows 'accbook'
	columns 'cfDate'
}
dataRequest {
	table 'mcf',allDimensions: ['accbook','currency','cfDate']
	fields 'sum(endingBalance) value'
	groupFields 'accbook,cfDate'
	contextParams (['date','organ','currency','staticModel'])
}
heads {
	accbook {
		pager {
			perPage 5
		}
	}
}