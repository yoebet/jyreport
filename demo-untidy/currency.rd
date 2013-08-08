
code 'Cur'
name '多币种演示'
dimensions {
	accbook {
		appendBetweenParam 'yes'
	}
	currency loadErate: false
}
layout {
	rows 'accbook'
	columns 'currency'
}
dataRequest {
	table 'mcf',allDimensions: ['accbook','currency','cfDate']
	fields 'accbook,currency,sum(endingBalance) value'
	groupFields 'accbook,currency'
	params {
		date 'CONTEXT[]'
		cfDate 'CONTEXT[]'
		staticModel 'CONTEXT[]'
	}
}
heads {
	accbook {
		params {
			//id (['in',[10021,10022,10025,10023,10028,10024,10030,10027,10026,10029]])
			//id (['between',[10021,10030]])
		}
	}
}