
code 'cf_dcf'
name '日现金流'

dimensions {
	accbook null
	cfDate name:'cfDate',headProcessor:DcfProcessor
}
dataRequest {
	table 'dcf',{
		allDimensions (['organ','accbook','currency','cfDate'])
	}
	fields 'sum(endingBalance) value'
    groupFields 'accbook,cfDate'
	contextParams (['date','organ','currency','staticModel'])
}

dataGrids {
	apply selectRows({children!=null}),{
		formula {sumChildren()}
	}
}