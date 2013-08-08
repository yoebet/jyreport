

code 'G01'
name '资产负债表'
dimensions {
	rowdim RdDimension
	date DateDimension,{
		headModels ([parseDate('2012-3-31'),parseDate('2012-4-30')])
	}
	coldim CdDimension
	dummy {
		modelClass HashMap
		dummy true
		name 'dummy'
		headModels ([[id:1212,name:'X']])
	}
}
layout {
	rows (['date','rowdim'])
	columns (['coldim','dummy'])
}
dataRequest {
	table DummyTable
	params {
		organ cw({context['organ']})
		currency 'CONTEXT[]'
	}
}
derivedHeads {
	rowdim ([SUM_HEAD()])
}
dataGrids {
	apply buildSelector([date:parseDate('2012-3-31'),coldim:2]), {
		value {12300000.0d}
	}
	apply buildSelector([-3,0..1]), {
		formula {60000000.0d}
	}
	apply buildSelector([date:parseDate('2012-3-31')],{col==0}), {
		value {32100000.0d}
	}
	apply {
		or {
			select ([0,0])
			select ([1,1])
		}
//			select buildSelector([0,0]).or ([1,1])
		template {
			formula {70000000.0d}
		}
	}
	apply {
		or {
			and {
				select buildSelector([2..5,0..1]).not([3,1])
				select ([[3,4],0..1])
			}
			select ([6,0..1])
		}
		template {
			formula {90000000.0d}
		}
	}
	apply buildSelector([9..11,0..1],{row==9}), {
		value {80000000.0d}
	}
}
