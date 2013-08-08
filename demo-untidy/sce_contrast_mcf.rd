
code 'sce_contrast_mcf'
name '月现金流对照'
dimensions {
	accbook null
	monthEnd name:'cfDate'
	scene null
}
layout {
	rows 'accbook'
	columns (['cfDate','scene'])
}
dataRequest {
	name 'scene1'
	table 'sceMcf',{
		allDimensions (['accbook','cfDate'])
	}
	fields 'accbook,cfDate,endingBalance value'
	contextParams (['scene1','foreignToHome'])
}
dataRequest {
	name 'scene2'
	table 'sceMcf',{
		allDimensions (['accbook','cfDate'])
	}
	fields 'accbook,cfDate,endingBalance value'
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
