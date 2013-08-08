
code 'str_liquid'
name '流动性缺口'
dimensions {
	accbook null
	liquidGap null
}
dataRequest {
	table 'sceLiquid',{
		allDimensions (['accbook','liquidGap'])
	}
	fields 'accbook,liquidGap,principal value'
	contextParams (['stress'])
}

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
