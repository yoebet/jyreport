
code 'cf_interest_accrual'
name '利息收入（权责发生制）'
dimensions {
	accbook null
	monthEnd name:'cfDate'
}
dataRequest {
	table 'mcfd',{
		allDimensions (['organ','accbook','currency','cfDate'])
	}
	fields 'sum(interestAccrual) value'
    groupFields 'accbook,cfDate'
	contextParams (['date','organ','currency','staticModel'])
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
}
