
code 'sub_dim'
name '子维度演示'
dimensions {
	accbook {
		subDimension 'repriceFreq',{
			name 'repriceFrequency'
		}
		appendSubHeadCallback { thisHead,subHead->
			def accitemId=thisHead.model?.id
			accitemId.toInteger() % 2 == 0
		}
	}
	monthEnd name:'cfDate'
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