
code 'lower_dim'
name '次级维度演示'
dimensions {
	accbook {
		appendLowerHeadCallback { thisHead,lowerHead->
			def accitemId=thisHead.model?.id
			accitemId.toInteger() % 2 == 0
		}
	}
	repriceFreq name:'repriceFrequency'
	monthEnd name:'cfDate'
}
layout {
	rows (['accbook','repriceFrequency'])
	columns 'cfDate'
}
dataRequest {
	table 'mcfd',allDimensions: ['organ','accbook','currency','cfDate',
		'repriceFrequency']
	fields 'sum(endingBalance) value'
	groupFields 'accbook,cfDate,repriceFrequency'
	contextParams (['date','organ','currency','staticModel'])
}
heads {
	accbook {
		pager {
			perPage 20
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