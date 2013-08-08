
code 'Cur'
name '币种折算演示'
dimensions {
	accbook {
		appendBetweenParam 'yes'
	}
	currency loadErate: true
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
			id (['between',[10021,10030]])
		}
	}
}
derivedHeads {
	currency {
		head {
			name '美元折人民币'
			formula {
				v([currency:'14'])*100/(rc.erateMap['14'])
			}
		}
		head {
			name '外币折人民币'
			aggregator {curvs->
				currencyExchange(curvs,'01')
			}
		}
		head {
			name '外币折美元'
			aggregator {curvs->
				currencyExchange(curvs,'14')
			}
		}
		head {
			name '本外币合计'
			aggregator {curvs->
				currencyExchange(curvs,'01',true)
			}
		}
	}
}
