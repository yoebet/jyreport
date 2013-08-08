
code 'sce_mcf'
name '月现金流'
dimensions {
	accbook null
	monthEnd name:'cfDate'
}
dataRequest {
	table 'sceMcf',{
		allDimensions (['accbook','cfDate'])
	}
	fields 'accbook,cfDate,endingBalance value'
	contextParams (['scene','foreignToHome'])
}
dataGrids {
	apply selectRows({children!=null}),{
		formula {sumChildren()}
	}
}
