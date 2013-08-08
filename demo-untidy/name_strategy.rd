
code 'name_strategy'
name '命名策略'
dimensions {
	accbook {
		//		nameStrategy {
		//			propertyToField {
		//				it.replaceAll('[A-Z]',{'0_'+it[0].toLowerCase()})
		//			}
		//			fieldToProperty {
		//				it.toLowerCase().replaceAll('z_(.)',{it[1].toUpperCase()})
		//			}
		//		}
	}
	monthEnd name:'cfDate'
}
dataRequest {
	table 'mcf',{
		allDimensions (['organ','accbook','currency','cfDate'])
		nameStrategy {
			propertyToField {
				it.replaceAll('[A-Z]',{'_'+it[0].toLowerCase()})
			}
			fieldToProperty {
				it.toLowerCase().replaceAll('_(.)',{it[1].toUpperCase()})
			}
		}
	}
	fields 'accbook,cfDate,endingBalance value'
	contextParams (['date','organ','currency','staticModel'])
}
heads {
	cfDate {
		pager {
			perPage 3
			page 2
		}
	}
}