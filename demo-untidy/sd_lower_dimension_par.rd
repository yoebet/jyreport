
code 'lower_dim'
name '动态次级维度演示'
dimensions {
	accbook {
		appendLowerHeadCallback { thisHead,lowerHead->
			def term=lowerHead.model?.id
			term.toInteger() % 2 == 0
		}
		lowerDimensionExpandParams { thisHead->
			def accitem=thisHead.model
			[termSetId: accitem.termSetId as Integer]
		}
	}
	term null
	monthEnd name:'cfDate'
}
layout {
	rows (['accbook','term'])
	columns 'cfDate'
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
		pager {
			perPage 10
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