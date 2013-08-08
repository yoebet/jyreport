
code 'cf_day_gap'
name '逐日缺口'

dimensions {
	accbook null
	cfDate name:'cfDate',headProcessor:DlgapProcessor
}
dataRequest {
	table 'dcf',{
		allDimensions (['organ','accbook','currency','cfDate'])
	}
	fields 'sum(principal) value'
    groupFields 'accbook,cfDate'
	contextParams (['date','organ','currency','staticModel'])
}

derivedHeads{
    accbook ([
                new Head(name:'逐日缺口',aggregate:true,
                    formula:{
                        def grids=thisColGrids({parent==null})
                        def a=grids.find{it.rowHeadGrid.model?.alType=='A'}
                        def l=grids.find{it.rowHeadGrid.model?.alType=='L'}
                        if(a && l){
                            a-l
                        }
                    }
                ),
                new Head(name:'缺口累计',aggregate:true,
                    formula:{
                        (col==0)? higher:(left+higher)
                })
		   ])
}

dataGrids {
	apply selectRows({children!=null}),{
		formula {sumChildren()}
	}
}