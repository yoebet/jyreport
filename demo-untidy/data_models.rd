
code 'G01'
name '资产负债表'
dimensions {
	rdId RdDimension,[name:'rdId']
	cdId CdDimension,{
		name 'cdId'
	}
}
dataRequest 'dummy', {
	table {
		name 'accbal'
		allDimensions (['rdId','cdId'])
		dataModels ([
				[rdId:1,cdId:1,value:164534.5],
				[rdId:11,cdId:1,value:534334.24],
				[rdId:12,cdId:1,value:1833334.24],
				[rdId:2,cdId:1,value:641563.23],
				[rdId:21,cdId:1,value:631563.23],
				[rdId:22,cdId:1,value:611563.23],
				[rdId:2,cdId:2,value:691563.23],
				[rdId:21,cdId:2,value:621563.23],
				[rdId:22,cdId:2,value:595422.05],
				[rdId:1,cdId:2,value:354334.24],
				[rdId:11,cdId:2,value:753334.24],
				[rdId:12,cdId:2,value:156334.24]
			])
	}
}