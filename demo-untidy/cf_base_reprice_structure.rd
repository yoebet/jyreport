
code 'cf_base_reprice_structure'
name '基期重定价结构'
dimensions {
	accbook fetchParents:true
	repriceFreq null
}
dataRequest {
	table 'baseAccOrg',{
		allDimensions (['organ','currency','accbook','repriceFreq'])
	}
	fields 'sum(balance) value'
	groupFields 'accbook,repriceFreq'
	contextParams (['date','organ','currency','staticModel'])
}
heads {
	accbook {
		params {
			reprice true
		}
	}
}
dataGrids {
	apply selectRows({children!=null}),{
		formula {sumChildren()}
	}
}
