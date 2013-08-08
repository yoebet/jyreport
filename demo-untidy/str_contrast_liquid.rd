
code 'str_contrast_liquid'
name '流动性缺口对照'
dimensions {
	accbook null
	liquidGap null
	stress null
}
layout {
	rows 'accbook'
	columns (['liquidGap','stress'])
}
dataRequest {
	name 'stress1'
	table 'sceLiquid',{
		allDimensions (['accbook','liquidGap'])
	}
	fields 'accbook,liquidGap,principal value'
	contextParams (['stress1'])
}
dataRequest {
	name 'stress2'
	table 'sceLiquid',{
		allDimensions (['accbook','liquidGap'])
	}
	fields 'accbook,liquidGap,principal value'
	contextParams (['stress2'])
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
					(col<2)? higher:(cr(-2)+higher)
                })
		])
}
dataGrids {
    apply selectRows({children!=null}),{
		formula {sumChildren()}
	}
	apply sel([scene:'stress2']),{
		params {
			dataRequest 'stress2'
		}
	}
}
