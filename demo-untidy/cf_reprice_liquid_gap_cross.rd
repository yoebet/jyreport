
code 'cf_reprice_liquid_gap_cross'
name '重定价、流动性缺口交叉'
dimensions {
	accbook {
		subDimension 'liquidGap'
		appendSubHeadCallback { thisHead,subHead->
			thisHead.model?.hasTerm
		}
	}
	repriceGap null
}
dataRequest {
	table 'crossGap'
	contextParams (['date','organ','currency','staticModel'])
}
dataGrids {
	apply selectRows({children!=null}),{
		formula {sumChildren()}
	}
}
