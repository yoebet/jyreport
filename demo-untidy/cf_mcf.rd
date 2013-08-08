
code 'cf_mcf'
name '月现金流'
dimensions {
	accbook null
	monthEnd name:'cfDate'
}
dataRequest {
	table 'mcf',{
		allDimensions (['organ','accbook','currency','cfDate'])
	}
	fields 'accbook,cfDate,endingBalance value'
	contextParams (['date','organ','currency','staticModel'])
}
dataGrids {
	apply selectRows({children!=null}),{
		formula {sumChildren()}
	}
}
