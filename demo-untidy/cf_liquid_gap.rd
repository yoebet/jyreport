
code 'cf_liquid_gap'
name '流动性缺口'
dimensions {
	accbook null
	liquidGap null
}
dataRequest {
	table 'liquidGap',{
		allDimensions (['organ','accbook','currency','liquidGap'])
	}
	fields 'accbook,liquidGap,principal value'
	contextParams (['date','organ','currency','staticModel'])
}

dataRequest StaticModelLgaplimitDataRequest

derivedHeads{
    accbook ([
        new Head(name:'流动性缺口',aggregate:true,
            formula:{
                def grids=thisColGrids({parent==null})
                def a=grids.find{it.rowHeadGrid.model?.alType=='A'}
                def l=grids.find{it.rowHeadGrid.model?.alType=='L'}
                if(a && l){
                    a-l
                }
            },
            evaluatedCallback:{
                    //下限额
                    def llimit=v0([liquidGap:params['liquidGap'],bal:'lvalue',dataRequest:'lgaplimit'])
                    //上限额
                    def ulimit=v0([liquidGap:params['liquidGap'],bal:'hvalue',dataRequest:'lgaplimit'])
                    if((llimit!=null && value<llimit) || (ulimit!=null && value>ulimit)){
                        if(properties==null){properties=[:]}
                        properties['cssClass']='lgap_out_of_limit'
                    }
            }
        ),
        new Head(name:'缺口累计',aggregate:true,
            formula:{
                (col==0)? higher:(left+higher)
            },
            evaluatedCallback:{
                    //下限额
                    def llimit=v0([liquidGap:params['liquidGap'],bal:'accumLvalue',dataRequest:'lgaplimit'])
                    //上限额
                    def ulimit=v0([liquidGap:params['liquidGap'],bal:'accumHvalue',dataRequest:'lgaplimit'])
                    if((llimit!=null && value<llimit) || (ulimit!=null && value>ulimit)){
                        if(properties==null){properties=[:]}
                        properties['cssClass']='lgap_out_of_limit'
                    }
            }
        )
    ])
}
dataGrids {
    apply selectRows({children!=null}),{
		formula {sumChildren()}
	}
}
