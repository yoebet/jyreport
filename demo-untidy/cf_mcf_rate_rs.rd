
code 'cf_mcf_rate_rs'
name '月现金流、加权利率（利率敏感帐户）'
dimensions {
	accbook fetchParents:true
	monthEnd name:'cfDate'
	target {
		headModels {
			model ([id:'principal',name:'发生额'])
			model ([id:'balance',name:'余额'])
			model ([id:'rate',name:'加权利率'])
		}
	}
}
layout {
	rows 'accbook'
	columns (['cfDate','target'])
}
dataRequest {
	table 'mcf',{
		allDimensions (['organ','accbook','currency','cfDate','target'])
		dataFieldsMap ([rate:'rate',balance:'endingBalance',principal:'principal'])
	}
	fields 'accbook,cfDate,endingBalance,rate,principal'
	contextParams (['date','organ','currency','staticModel'])
}
heads {
	accbook {
		params {
			rateMark (['not null'])
		}
	}
}
dataGrids {
	apply selectRows({children!=null}),{
		formula {sumChildren()}
	}
	apply selectColumns({head.value=='rate'}),{
		dataType 'percent'
	}
	apply selectColumns({head.value=='rate'},{children!=null}),{
		formula{
			def balanceSum=left.result
			if(balanceSum && balanceSum > 1.0){
				def wrate=children?.sum(0.0){it.left*it}
				wrate/balanceSum
			}else{
				0.0
			}
		}
	}
}
