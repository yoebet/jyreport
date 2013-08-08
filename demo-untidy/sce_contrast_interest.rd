
code 'sce_contrast_interest'
name '利息收入对照'
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
	fields 'accbook,cfDate,sum(interest) value'
    groupFields 'accbook,cfDate'
	contextParams (['scene1','foreignToHome'])
}
dataRequest {
	name 'scene2'
	table 'sceMcf',{
		allDimensions (['accbook','cfDate'])
	}
	fields 'accbook,cfDate,sum(interest) value'
    groupFields 'accbook,cfDate'
	contextParams (['scene2','foreignToHome'])
}
derivedHeads{
    accbook ([new Head(name:'净利息收入',aggregate:true,formula:{
        def grids=thisColGrids({parent==null})
        def a=grids.find{it.rowHeadGrid.model?.alType=='A'}
        def l=grids.find{it.rowHeadGrid.model?.alType=='L'}
        if(a && l){
            a-l
         }
    })])
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
