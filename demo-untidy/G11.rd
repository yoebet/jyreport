
code 'G11'
name '流动性比例监测表'
dimensions {
	rowdim {
		dummy true
        hierarchical true
        headModels ([
            [id:101,name:'现金',parentId:1],[id:102,name:'黄金',parentId:1],[id:103,name:'超额准备金',parentId:1],
            [id:104,name:'一个月内同行业往来',parentId:1],[id:105,name:'一个月内应收利息',parentId:1],[id:106,name:'一个月内合格贷款',parentId:1],
            [id:107,name:'一个月内债券投资',parentId:1],[id:108,name:'变现证券透支',parentId:1],[id:109,name:'可变现资产',parentId:1],
            [id:110,name:'流动性资产总和',parentId:1],
            [id:201,name:'活期存款',parentId:2],[id:202,name:'一个月内定期存款',parentId:2],[id:203,name:'一个月同业往来款项',parentId:2],
            [id:204,name:'一个月内已发行债券',parentId:2],[id:205,name:'一个月内应付利息',parentId:2],[id:206,name:'一个月内中央银行借款',parentId:2],
            [id:207,name:'一个月内其他负债',parentId:2],[id:208,name:'流动性负债总和',parentId:2], 
            [id:1,name:'流动性资产'],[id:2,name:'流动性负债'],[id:3,name:'流动性比例'],[id:4,name:'本月平均流动性资产'],
            [id:5,name:'本月平均流动性负债'],[id:6,name:'本月平均流动性比列'],
            [id:7,name:'月度最低流动性比例'],[id:8,name:'一个月到期存款金额'],[id:9,name:'用于质押的有关贷款金额']
        ])  
    }
	
    currency {
        text '余额'
		loadErate true
		staticHeads {
			head {
				model ([code:'01',name:'人民币'])
			}
			head {
				derived true
				aggregate true
				model ([code:'foreign',name:'外币折人民币'])
				aggregator {curvs->
					currencyExchange(curvs,'01')
				}
			}
			head {
				derived true
				aggregate true
				model ([code:'sum',name:'本外币合计'])
				formula {
					111111111
				}
			}
		}
	}

}

dataRequest {
    table 'glAllBranch'
    params {
        date 'context[]'
    }
}

dataRequest {
    table 'cfLiquid',allDimensions: ['currency','item','date']
    fields 'sum(balance) value'
    groupFields 'subject_number,currency_code'
    params {
        date 'context[]'
		currency 'context[]'
        staticModel 'context[]'
        liquidGapTerm cw({["<=",context['liquidGapDays']]})
    }
}
//手工维护
dataRequest ManualDataRequest


dataGrids {
	apply sel([rowdim:101,currency:['01','foreign']]), {
        params ([item:'S1001',bal:'debit',dataRequest:'glAllBranch'])
	}
	apply sel([rowdim:102,currency:['01','foreign']]), {
        params ([item:'S1431',bal:'debit',dataRequest:'glAllBranch'])
	}
	apply sel([rowdim:103,currency:['01','foreign']]), {
		formula {
                    v([item:"S1002",bal:'debit',dataRequest:'glAllBranch']) -
                    0.000 //应缴准备金余额
                }
	}
	apply sel([rowdim:104,currency:['01','foreign']]), {
        formula {sumItemsWithMinus(['S1011','S1012','S1013','S1014','S1031','S1111','1404'],['S1032','S2017','S2018','S2019','S2111','S2020'],[dataRequest:'cfLiquid'])}
	}
	apply sel([rowdim:105,currency:['01','foreign']]), {
		formula {sumItems(['S1132','0220','0221'],[dataRequest:'cfLiquid'])}
	}
	apply sel([rowdim:106,currency:['01','foreign']]), {
		formula {sumItems(['S1301','S1302','S1303','S1304','S1306'],[dataRequest:'cfLiquid'])}
	}
	apply sel([rowdim:107,currency:['01','foreign']]), {
		formula {0.000}
	}
	apply sel([rowdim:108,currency:['01','foreign']]), {
		formula {0.000}
	}
	apply sel([rowdim:109,currency:['01','foreign']]), {
        formula {
                sumItems(['1003','1305','1811','1901'],[bal:'debit',dataRequest:'glAllBranch'])+
                mathPositive(v([item:"0031",bal:'debit',dataRequest:'glAllBranch']),v([item:"2006",bal:'credit',dataRequest:'glAllBranch']),0)+
                //下面是手工维护
                v0([dict:'rmblccp1',dataRequest:'manualData'])+
                v0([dict:'rmblccp2',dataRequest:'manualData'])+
                v0([dict:'rmblccp3',dataRequest:'manualData'])
        }
	}
	apply sel([rowdim:110,currency:['01','foreign']]), {
		formula {
            (-9..-1).sum{rowRel(it)}
        }
	}   
	apply sel([rowdim:201,currency:['01','foreign']]), {
        formula {sumItems(['2001','2003','2005','1404'],[bal:'credit',dataRequest:'glAllBranch'])}
	}
	apply sel([rowdim:202,currency:['01','foreign']]), {
		formula {sumItems(['S2002','S2004','S2014'],[dataRequest:'cfLiquid'])}
	}
	apply sel([rowdim:203,currency:['01','foreign']]), {
        formula{
            mathPositive(sumItems(['S2017','S2019','S2111'],[dataRequest:'cfLiquid']),sumItems(['S1011','S1013','S1111'],[dataRequest:'cfLiquid']),0)
        }
	}
	apply sel([rowdim:204,currency:['01','foreign']]), {
		formula {sumItems(['S2502'],[dataRequest:'cfLiquid'])}
	}
	apply sel([rowdim:205,currency:['01','foreign']]), {
		formula {sumItems(['S2231','0450','1451','3002'],[dataRequest:'cfLiquid'])}
	}
	apply sel([rowdim:206,currency:['01','foreign']]), {
		formula {sumItems(['2015'],[dataRequest:'cfLiquid'])}
	}
	apply sel([rowdim:207,currency:['01','foreign']]), {
           formula {
                sumItems(['S2007','S2011','SS2012','S2013','S2014','S2101','S2221','S2401','S2711','S2801','S2901','S3031','S30411','S30412','S30413','S30414','S30415','S30416','S30417','S30419','S3051','S3301'],[bal:'credit',dataRequest:'glAllBranch'])+
                mathPositive(sumItems(['S2006'],[bal:'credit',dataRequest:'glAllBranch']),sumItems(['0013'],[bal:'debit',dataRequest:'glAllBranch']),0)-
                sumItems(['S2014'],[dataRequest:'cfLiquid'])
           }
	}
	apply sel([rowdim:208,currency:['01','foreign']]), {
		formula {
            (-7..-1).sum{rowRel(it)}
        }
	}
 
    //第3列 求和
    apply sel([0..26,2]), {
        formula {colRel(-1)+colRel(-2)}
	}
    //设置这行的文本格式 百分比 3.流动性比例 (项目1.10/项目2.8×100％)
    apply sel([rowdim:3,currency:['01','foreign','sum']]),{
       // dataType new PercentType()
        dataType 'percent'
    }
    //3.流动性比例 (项目1.10/项目2.8×100％)
	apply sel([rowdim:3,currency:['01','foreign','sum']]),{
		formula {rowRel(-10)/rowRel(-1)}
	}
    //4.本月平均流动性资产
	apply sel([rowdim:4,currency:['01','foreign','sum']]),{//目前只是本期的
		formula {rowRel(-11)}
	}
    //5.本月平均流动性负债
	apply sel([rowdim:5,currency:['01','foreign','sum']]),{//目前只是本期的
		formula {rowRel(-3)}
	}
    //设置这行的文本格式 百分比 6.本月平均流动性比例(项目4./项目5.×100％)
    apply sel([rowdim:6,currency:['01','foreign','sum']]),{
       // dataType new PercentType()
        dataType 'percent'
    }
    //6.本月平均流动性比例(项目4./项目5.×100％)
	apply sel([rowdim:6,currency:['01','foreign','sum']]),{
		formula {(rowRel(-2)/rowRel(-1))}
	}
    //7.月度最低流动性比例（％）
	apply sel([24,0..2]), {
		formula {00000.0}
	}
    //8.一个月内到期用于质押的存款金额
	apply sel([25,0..2]), {
		formula {00000.0}
	}
    //9.项目8.用于质押的有关贷款金额
	apply sel([26,0..2]), {
		formula {00000.0}
	}

 //将1.流动性资产 2.流动性负债 所在行，为空 
   apply sel([rowdim:[1,2],currency:['01','foreign','sum']]), {
		formula {
            
        }
	}
}