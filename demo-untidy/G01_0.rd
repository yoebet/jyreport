
code 'G01'
name '资产负债表'
dimensions {
	rowdim RdDimension
	coldim CdDimension
}
layout {
	rows 'rowdim'
	columns 'coldim'
}
dataRequest 'dummy', {
	table DummyTable
	params {
		date parseDate('2012-5-30')
		organ '110101'
		currency '01'
	}
}
derivedHeads {
	coldim {
		head new Head(name:'合计',aggregate:true,formula:SUM,position:0)
	}
}