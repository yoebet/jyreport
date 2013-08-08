
code 'cf_reprice_gap'
name '重定价缺口'
dimensions {
	accbook fetchParents:true
	repriceGap null
}
dataRequest {
	table 'repriceGap',{
		allDimensions (['organ','accbook','currency','repriceGap'])
	}
	fields 'sum(endingBalance) endingBalance'
	groupFields 'accbook,repriceGap'
	contextParams (['date','organ','currency','staticModel'])
}
heads {
	accbook {
		params {
			reprice true
		}
	}
}
dataRequest StaticModelRgaplimitDataRequest

derivedHeads{
    accbook ([
        new Head(name:'重定价缺口',aggregate:true,
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
                    def llimit=v0([riquidGap:params['riquidGap'],bal:'lvalue',dataRequest:'rgaplimit'])
                    //上限额
                    def ulimit=v0([riquidGap:params['riquidGap'],bal:'hvalue',dataRequest:'rgaplimit'])
                    if((llimit!=null && value<llimit) || (ulimit!=null && value>ulimit)){
                        if(properties==null){properties=[:]}
                        properties['cssClass']='rgap_out_of_limit'
                    }
            }
        ),
        new Head(name:'缺口累计',aggregate:true,
            formula:{
                (col==0)? higher:(left+higher)
            },
            evaluatedCallback:{
                    //下限额
                    def llimit=v0([riquidGap:params['riquidGap'],bal:'accumLvalue',dataRequest:'rgaplimit'])
                    //上限额
                    def ulimit=v0([riquidGap:params['riquidGap'],bal:'accumHvalue',dataRequest:'rgaplimit'])
                    if((llimit!=null && value<llimit) || (ulimit!=null && value>ulimit)){
                        if(properties==null){properties=[:]}
                        properties['cssClass']='rgap_out_of_limit'
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
