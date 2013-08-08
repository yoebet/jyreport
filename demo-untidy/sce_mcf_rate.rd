
code 'sce_mcf_rate'
name '月现金流、加权利率'
dimensions {
	accbook null
	monthEnd name:'cfDate'
	target {
		headModels {
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
	table 'sceMcf',{
		allDimensions (['accbook','cfDate','target'])
		dataFieldsMap ([rate:'rate',balance:'endingBalance'])
	}
	fields 'accbook,cfDate,endingBalance,rate'
	contextParams (['scene','foreignToHome'])
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
