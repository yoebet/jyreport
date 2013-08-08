
code 'sce_contrast_nv'
name '新业务量对照'
dimensions {
	accbook null
	monthEnd name:'cfDate',fromNextMonth:true
	scene null
}
layout {
	rows 'accbook'
	columns (['cfDate','scene'])
}
dataRequest {
	name 'scene1'
	table 'sceNv',{
		allDimensions (['accbook','cfDate'])
	}
	fields 'sum(endingBalance) value'
	groupFields 'accbook,cfDate'
	contextParams (['scene1','foreignToHome'])
}
dataRequest {
	name 'scene2'
	table 'sceNv',{
		allDimensions (['accbook','cfDate'])
	}
	fields 'sum(endingBalance) value'
	groupFields 'accbook,cfDate'
	contextParams (['scene2','foreignToHome'])
}
dataGrids {
	apply selectRows({children!=null}),{
		formula {sumChildren()}
	}
	apply sel([scene:'scene2']),{
		params {
			dataRequest 'scene2'
		}
	}
}
