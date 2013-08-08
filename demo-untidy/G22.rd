
code 'G22'
name '流动性比例监测表'
dimensions {
	rowdim {
		dummy true
        text '项目'
        hierarchical true
        headModels ([
            [id:101,name:'1.1现金',parentId:1],
            [id:102,name:'1.2黄金',parentId:1],
            [id:103,name:'1.3超额准备金存款',parentId:1],
            [id:104,name:' 1.4一个月内到期的同业往来款项轧差后资产方净额',parentId:1],
            [id:105,name:'1.5一个月内到期的应收利息及其他应收款',parentId:1],
            [id:106,name:'1.6一个月内到期的合格贷款',parentId:1],
            [id:107,name:'1.7一个月内到期的债券投资',parentId:1],
            [id:108,name:'1.8在国内外二级市场上可随时变现的证券投资（不包括项目1.7的有关项目）',parentId:1],
            [id:109,name:'1.9其他一个月内到期可变现的资产（剔除其中的不良资产）',parentId:1],
            [id:110,name:'1.10流动性资产总和 (项目1.1~项目1.9之和)',parentId:1],
            [id:201,name:'2.1活期存款（不含财政性存款）',parentId:2],
            [id:202,name:'2.2一个月内到期的定期存款（不含财政性存款）',parentId:2],
            [id:203,name:'2.3一个月内到期的同业往来款项轧差后负债方净额',parentId:2],
            [id:204,name:'2.4一个月内到期的已发行的债券',parentId:2],
            [id:205,name:'2.5一个月内到期的应付利息和各项应付款',parentId:2],
            [id:206,name:'2.6一个月内到期的向中央银行借款',parentId:2],
            [id:207,name:'2.7其他一个月内到期的负债',parentId:2],
            [id:208,name:' 2.8流动性负债总和',parentId:2], 
            [id:1,name:'1.流动性资产'],
            [id:2,name:'2.流动性负债'],
            [id:3,name:'3.流动性比例 (项目1.10/项目2.8×100％)'],
            [id:4,name:'4.本月平均流动性资产'],
            [id:5,name:'5.本月平均流动性负债'],
            [id:6,name:'6.本月平均流动性比例(项目4./项目5.×100％)'],
            [id:7,name:'7.月度最低流动性比例（％）'],
            [id:8,name:'8.一个月内到期用于质押的存款金额'],
            [id:9,name:'9.项目8.用于质押的有关贷款金额']
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
		organ 'context[]'
        staticModel 'context[]'
        liquidGapTerm (["between",[0,31]])
    }
}
dataRequest {
    table 'cfLiquid',{
        name 'cfLiquidOverdue'
        allDimensions (['currency','item','date'])
    }
    fields 'sum(balance) value'
    groupFields 'subject_number,currency_code'
    params {
        date 'context[]'
		organ 'context[]'
        staticModel 'context[]'
        cat 'loan'
        liquidGapTerm (["between",[-30,-1]])
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
    //103
	apply sel([rowdim:103,currency:['01']]), {
		formula {
            v([item:"S1002",bal:'debit',dataRequest:'glAllBranch']) - v0([dict:'zbjye_01',dataRequest:'manualData']) //应缴准备金余额
        }
	}
    apply sel([rowdim:103,currency:['foreign']]), {
		formula {
            v([item:"S1002",bal:'debit',dataRequest:'glAllBranch']) - v0([dict:'zbjye_for',dataRequest:'manualData']) //应缴准备金余额
        }
	}
	apply sel([rowdim:104,currency:['01','foreign']]), {
        formula {sumItemsWithMinus(['S1011','S1012','S1013','S1014','S1031','S1111','1404'],['S1032','S2017','S2018','S2019','S2111','S2020'],[dataRequest:'cfLiquid'])}
	}
	apply sel([rowdim:105,currency:['01','foreign']]), {
		formula {sumItems(['S1132','0220','0221'],[dataRequest:'cfLiquid'])}
	}
	apply sel([rowdim:106,currency:['01','foreign']]), {
        formula {sumItems(['S1301','S1302','S1303','S1304','S1306'],[dataRequest:'cfLiquid'])+sumItems(['S1301','S1302','S1303','S1304'],[dataRequest:'cfLiquidOverdue'])}       
	}
    //107
	apply sel([rowdim:107,currency:['01']]), {
		formula {
            v0([dict:'oneMzqtz_01',dataRequest:'manualData'])
        }
	}
    apply sel([rowdim:107,currency:['foreign']]), {
		formula {
            v0([dict:'oneMzqtz_for',dataRequest:'manualData'])
        }
	}
    //108
	apply sel([rowdim:108,currency:['01']]), {
		formula {
            v0([dict:'zqtz_01',dataRequest:'manualData'])
        }
	}
    apply sel([rowdim:108,currency:['foreign']]), {
		formula {
            v0([dict:'zqtz_for',dataRequest:'manualData'])
        }
	}
	apply sel([rowdim:109,currency:['01','foreign']]), {
        formula {
            sumItems(['S1003','S1305','S1811','S1901'],[bal:'debit',dataRequest:'glAllBranch'])+
            mathPositive(v([item:"0031",bal:'debit',dataRequest:'glAllBranch']),v([item:"2006",bal:'credit',dataRequest:'glAllBranch']),0)+
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
        formula {sumItems(['S2001','S2003','S2005','1404'],[bal:'credit',dataRequest:'glAllBranch'])}
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
		formula {sumItems(['S2015'],[dataRequest:'cfLiquid'])}
	}
	apply sel([rowdim:207,currency:['01','foreign']]), {
           formula {
                sumItems(['S2007','S2011','S2012','S2013','S2014','S2101','S2221','S2401','S2711','S2801','S2901','S3031','S30411','S30412','S30413','S30414','S30415','S30416','S30417','S30419','S3051','S3301'],[bal:'credit',dataRequest:'glAllBranch'])+
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
        dataType 'percent'
    }
    //3.流动性比例 (项目1.10/项目2.8×100％)
	apply sel([rowdim:3,currency:['01','foreign','sum']]),{
		formula {rowRel(-10)/rowRel(-1)}
	}
    //4.本月平均流动性资产
	apply sel([rowdim:4,currency:['01']]),{
		formula {
            (rowRel(-11)+ v0([dict:'zc_01',dataRequest:'manualData']) )/2
        }
	}
    apply sel([rowdim:4,currency:['foreign']]),{
		formula {
            (rowRel(-11)+ v0([dict:'zc_for',dataRequest:'manualData']) )/2
        }
	}
    apply sel([rowdim:4,currency:['sum']]),{
		formula {
            colRel(-2)+colRel(-1)
        }
	}
    //5.本月平均流动性负债
    apply sel([rowdim:5,currency:['01']]),{
		formula {
            (rowRel(-3)+ v0([dict:'fz_01',dataRequest:'manualData']) )/2
        }
	}
    apply sel([rowdim:5,currency:['foreign']]),{
		formula {
            (rowRel(-3)+ v0([dict:'fz_for',dataRequest:'manualData']) )/2
        }
	}
    apply sel([rowdim:5,currency:['sum']]),{
		formula {
            colRel(-2)+colRel(-1)
        }
	}
    //设置这行的文本格式 百分比 6.本月平均流动性比例(项目4./项目5.×100％)
    apply sel([rowdim:6,currency:['01','foreign','sum']]),{
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
	apply sel([25,0]), {
		formula { v0([dict:'ck_01',dataRequest:'manualData']) }
	}
    apply sel([25,1]), {
		formula { v0([dict:'ck_for',dataRequest:'manualData']) }
	}
    apply sel([25,2]), {
		formula {colRel(-2)+colRel(-1)}
	}
    //9.项目8.用于质押的有关贷款金额
	apply sel([26,0]), {
		formula { v0([dict:'dk_01',dataRequest:'manualData']) }
	}
    apply sel([26,1]), {
		formula { v0([dict:'dk_for',dataRequest:'manualData']) }
	}
    apply sel([26,2]), {
		formula {colRel(-2)+colRel(-1)}
	}

    //将1.流动性资产 2.流动性负债 所在行，为空 
   apply sel([rowdim:[1,2],currency:['01','foreign','sum']]), {
		formula {
            
        }
	}
}