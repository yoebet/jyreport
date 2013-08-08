
code 'sce_nv'
name '新业务量'
dimensions {
	accbook null
	monthEnd name:'cfDate',fromNextMonth:true
}
dataRequest {
	table 'sceNv',{
		allDimensions (['accbook','cfDate'])
	}
	fields 'sum(endingBalance) value'
	groupFields 'accbook,cfDate'
	contextParams (['scene','foreignToHome'])
}
dataGrids {
	apply selectRows({children!=null}),{
		formula {sumChildren()}
	}
}
