
code 'cf_reprice_gap_freq_cross'
name '重定价缺口、周期交叉'
dimensions {
	accbook {
		fetchParents true
		subDimension 'repriceFreq'
	}
	repriceGap null
}
dataRequest {
	table 'repriceGap'
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
