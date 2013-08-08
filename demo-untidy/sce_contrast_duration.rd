
code 'sce_contrast_duration'
name '市值与久期对照'
dimensions {
	accbook null
	scene null
	target {
		headModels {
			model ([id:'market',name:'市值'])
			model ([id:'macaulayDuration',name:'麦考利久期'])
			model ([id:'modifiedDuration',name:'修正久期'])
		}
	}
}
layout {
	rows 'accbook'
	columns (['scene','target'])
}
dataRequest {
	name 'scene1'
	table 'sceDuration'
	fields 'accbook,marketValue,macaulayDuration,modifiedDuration,cashflowPresentValue'
	contextParams (['scene1'])
}
dataRequest {
	name 'scene2'
	table 'sceDuration'
	fields 'accbook,marketValue,macaulayDuration,modifiedDuration,cashflowPresentValue'
	contextParams (['scene2'])
}
dataGrids {
	apply selectRows({children!=null},{params['target']=='market'}),{
		formula {sumChildren()}
	}
	apply selectColumns({head.value=='macaulayDuration'||head.value=='modifiedDuration'}),{
		dataType 'numeric'
		formula {
			if(children==null){
				def pv=v([target:'cfpv'])
				// v([target:'macaulayDuration']) or v([target:'modifiedDuration'])
				def dur=v()
				[pv*dur,pv]
			}else{
				def durs=[0.0,0.0]
				children.each{
					def childDur=it.result
					durs[0]+=childDur[0]
					durs[1]+=childDur[1]
				}
				durs
			}
		}
		evaluatedCallback {
			def duration=value[0]
			def cfpv=value[1]
			if(cfpv > 0.0){
				value=duration/cfpv
			}else{
				value=0.0
			}
		}
	}
	apply sel([scene:'scene2']),{
		params {
			dataRequest 'scene2'
		}
	}
}
