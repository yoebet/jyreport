
code 'G21'
name '流动性缺口率报表'
dimensions {
	rowdim {
		modelClass HashMap
		valueProperty 'id'
        hierarchical true
        text '项目'
		dummy true
        headModels ([
            [id:1,name:'1.资产总计'],
			[id:11,name:'1.1现金',parentId:1],
			[id:12,name:'1.2存放中央银行款项',parentId:1],
			[id:13,name:'1.3存放同业款项',parentId:1],
			[id:14,name:'1.4拆放同业',parentId:1],
			[id:15,name:'1.5买入返售资产（不含非金融机构）',parentId:1],
			[id:16,name:'1.6各项贷款',parentId:1],
			[id:17,name:'1.7债券投资和债权投资',parentId:1],
			[id:18,name:'1.8其他有确定到期日的资产',parentId:1],
			[id:19,name:'1.9没有确定到期日的资产',parentId:1],
			[id:2,name:'2.表外收入'],
			[id:21,name:'2.1表外收入项--有确定到期日',parentId:2],
			[id:22,name:'2.2表外收入项--没有确定到期日',parentId:2],
			[id:3,name:'3.负债合计'],
			[id:31,name:'3.1向中央银行借款',parentId:3],
			[id:32,name:'3.2同业存放款项',parentId:3],
			[id:33,name:'3.3同业拆入',parentId:3],
			[id:34,name:'3.4卖出回购款项（不含非金融机构）',parentId:3],
			[id:35,name:'3.5各项存款',parentId:3],
			[id:351,name:'3.5.1其中：定期存款（不含财政性存款）',parentId:35],
			[id:352,name:'3.5.2活期存款（不含财政性存款）',parentId:35],
			[id:36,name:'3.6发行债券',parentId:3],
			[id:37,name:'3.7其他有确定到期日的负债',parentId:3],
			[id:38,name:'3.8没有确定到期日的负债',parentId:3],
			[id:4,name:'4.表外支出'],
			[id:41,name:'4.1表外支出项--有确定到期日',parentId:4],
			[id:42,name:'4.2表外支出项--没有确定到期日',parentId:4],
			[id:5,name:'5.到期期限缺口'],
			[id:6,name:'6.累计到期期限缺口'],
			[id:7,name:'7.附注:活期存款（不含财政性存款）']
        ])  
    }
	lpid {
		//dummy true
        valueProperty 'id'
        headModels ([[id:10069,name:'次日'],
					[id:10070,name:'2日至7日'],
					[id:10071,name:'8日至30日'],
					[id:10072,name:'31日至90日'],
					[id:10073,name:'91日至1年'],
					[id:10074,name:'1年以上'],
					[id:1,name:'未定期限'],
					[id:2,name:'逾期']])
                
    }
}

layout {
	rows 'rowdim'
	columns 'lpid'
}

dataRequest {
    table 'glAllBranch'
    params {
        date 'context[]'
    }    
}

dataRequest {
    table 'cfLiquid'
    fields 'subject_number,currency_code,liquid_gap_id,sum(balance) balance'
    groupFields 'subject_number,currency_code,liquid_gap_id'
    params {
        date 'context[]'
        organ 'context[]'
        staticModel 'context[]'
    }
}

//手工维护
dataRequest ManualDataRequest
dataRequest ErateDataRequest
derivedHeads {
	lpid {
		head SUM_HEAD()
	}
}

dataGrids {
    apply selectAll(),{
        aggregator {curvs ->
            currencyExchange2(curvs,'01',true)
        }
        formula{0.000}
    }
    //1.资产总计
	apply buildSelector([rowdim:1,lpid:10069..10074]), { 
		formula {sumChildren()}
	}
    apply buildSelector([rowdim:1,lpid:[1,2]]), { 
		formula {sumChildren()}
	}

    //1.1现金
	apply buildSelector([rowdim:11,lpid:10069]), {
        formula{
           v0([item:"S1001",bal:'debit',dataRequest:'glAllBranch'])
        }
	}
	
   //1.2 存放中央银行款项
   apply sel([rowdim:12,lpid:10069]),{
        formula{
           v0([item:"S1002",bal:'debit',dataRequest:'glAllBranch'])-
           0     //准备金余额
        }
   }

   apply sel([rowdim:12,lpid:1]),{
        formula{
           0-     //准备金余额
           v0([item:"0013",bal:'debit',dataRequest:'glAllBranch'])
        }
   }

    //1.3存放同业款项
	//隔夜
	apply buildSelector([rowdim:13,lpid:10069]), {
        formula {v([item:"S1011",bal:'debit',dataRequest:'glAllBranch'])-colRel(1)-colRel(2)-colRel(3)-colRel(4)-colRel(5)-colRel(7)}
	}
	//2-7日  --  一年以上
	apply buildSelector([rowdim:13,lpid:10070..10074]), {
        params ([item:'S1011',dataRequest:'cfLiquid'])
	}
	
	
	//逾期（待确定取数据的中间表）
	
    //1.4拆放同业
	//次日  --  一年以上
	apply buildSelector([rowdim:14,lpid:10069..10074]), {
        params ([item:'S1013',dataRequest:'cfLiquid'])
	}
	
	//逾期（待确定取数据的中间表）
	
    //1.5买入返售资产（不含非金融机构）（待确定数据）

    //1.6各项贷款

	//次日  --  一年以上
	apply buildSelector([rowdim:16,lpid:10069..10074]), {
		formula {sumItems(['S1301','S1302','S1303','S1304','S1305','S1306','S1307'],[dataRequest:'cfLiquid'])}
	}
	
	//逾期
	
    //1.7债券投资和债权投资（待确定数据）

    //1.8其他有确定到期日的资产

	//次日  
     apply sel([rowdim:18,lpid:10069]), {
		formula {
            sumItems0(['0220','0221','S1311','S1321','0031','S1003','S1431'],[bal:'debit',dataRequest:'glAllBranch'])-
            sumItems0(['S2313','S2314','S2006','S1432'],[bal:'credit',dataRequest:'glAllBranch'])+
            //资产负债共同类轧差后借产方余额
            sumItems0(['S3031','S30411','S30412','S30413','S30414','S30415','S30416','S30417','S30419','S3051','S3301'],[bal:'credit',dataRequest:'glAllBranch'])+
            //手工维护部分
            v0([dict:'rmblccp1',dataRequest:'manualData'])
        }
	}
	
	//2日-7日（手工维护）
    apply sel([rowdim:18,lpid:10070]), {
		formula {v0([dict:'rmblccp2',dataRequest:'manualData'])}
	}
	
	//8-30日（手工维护）
	apply sel([rowdim:18,lpid:10071]), {
		formula {v0([dict:'rmblccp3',dataRequest:'manualData'])}
	}
	
	//31-90日（手工维护）
	apply sel([rowdim:18,lpid:10072]), {
		formula {v0([dict:'rmblccp4',dataRequest:'manualData'])}
	}
	
	//91日-1年（手工维护）
	apply sel([rowdim:18,lpid:10073]), {
		formula {v0([dict:'rmblccp5',dataRequest:'manualData'])}
	}
	
	//1年以上（手工维护）
	apply sel([rowdim:18,lpid:10074]), {
		formula {v0([dict:'rmblccp6',dataRequest:'manualData'])}
	}
	
	//逾期
	apply buildSelector([rowdim:18,lpid:2]), {
        params ([item:'S1132',bal:'debit',dataRequest:'glAllBranch'])
	}
		
//1.9没有确定到期日的资产
	apply buildSelector([rowdim:19,lpid:1]), {
		formula{
            mathPositive(v0([item:"S1004",bal:'debit',dataRequest:'glAllBranch']),v0([item:"S2016",bal:'credit',dataRequest:'glAllBranch']),0.00)+mathPositive(v0([item:"S1441",bal:'debit',dataRequest:'glAllBranch']),v0([item:"S1442",bal:'credit',dataRequest:'glAllBranch']),0.00)+
            mathPositive(v0([item:"S1511",bal:'debit',dataRequest:'glAllBranch']),v0([item:"S1512",bal:'credit',dataRequest:'glAllBranch']),0.00)+mathPositive(v0([item:"S1521",bal:'debit',dataRequest:'glAllBranch']),sumItems0(['S1522','S1523'],[bal:'credit',dataRequest:'glAllBranch']),0.00)+
            v([item:"S1532",bal:'debit',dataRequest:'glAllBranch'])+
            mathPositive(v0([item:"S1601",bal:'debit',dataRequest:'glAllBranch']),sumItems0(['S1602','S1603'],[bal:'credit',dataRequest:'glAllBranch']),0.00)+
            mathPositive(v0([item:"S1604",bal:'debit',dataRequest:'glAllBranch']),v0([item:"S1605",bal:'credit',dataRequest:'glAllBranch']),0.00)+v0([item:"S1606",bal:'debit',dataRequest:'glAllBranch'])+
            mathPositive(v0([item:"S1701",bal:'debit',dataRequest:'glAllBranch']),sumItems0(['S1702','S1703'],[bal:'credit',dataRequest:'glAllBranch']),0.00)+
            sumItems0(['S1801','S1811','S1901'],[bal:'debit',dataRequest:'glAllBranch'])-v0([item:"S1309",bal:'credit',dataRequest:'glAllBranch'])-v0([item:"S1231",bal:'credit',dataRequest:'glAllBranch'])+
            v0([item:"S1221",bal:'debit',dataRequest:'glAllBranch'])-
            sumItems0(['0220','0221'],[bal:'debit',dataRequest:'glAllBranch'])+
            v0([dict:'pxzxkx',dataRequest:'manualData'])//手工维护
        }
		
	}

    //3负债合计
	apply buildSelector([rowdim:3,lpid:10069..10074]), { 
		formula {sumChildren()}
	}
    apply buildSelector([rowdim:3,lpid:[1,2]]), { 
		formula {sumChildren()}
	}

    //3.2同业存放款项（需要手工数据）
    apply sel([rowdim:32,lpid:10069]),{
        formula{
            (sumItemsWithMinus0(['S2017'],['1404','0408','0409'],[bal:'credit',dataRequest:'glAllBranch'])-0)/360+                //问题---0代表，最低贷方余额
            sumItems(['0408','0409'],[dataRequest:'cfLiquid'])
        }
    }
    
    apply sel([rowdim:32,lpid:10070]),{
        formula{
            (sumItemsWithMinus0(['S2017'],['1404','0408','0409'],[bal:'credit',dataRequest:'glAllBranch'])-0)/360*6+                //问题---0代表，最低贷方余额
            sumItems(['0408','0409'],[dataRequest:'cfLiquid'])
        }
    }

    apply sel([rowdim:32,lpid:10071]),{
        formula{
            (sumItemsWithMinus0(['S2017'],['1404','0408','0409'],[bal:'credit',dataRequest:'glAllBranch'])-0)/360*23+                //问题---0代表，最低贷方余额
            sumItems(['0408','0409'],[dataRequest:'cfLiquid'])
        }
    }
    apply sel([rowdim:32,lpid:10072]),{
        formula{
            (sumItemsWithMinus0(['S2017'],['1404','0408','0409'],[bal:'credit',dataRequest:'glAllBranch'])-0)/12*2+                //问题---0代表，最低贷方余额
            sumItems(['0408','0409'],[dataRequest:'cfLiquid'])
        }
    }
    apply sel([rowdim:32,lpid:10073]),{
        formula{
            (sumItemsWithMinus0(['S2017'],['1404','0408','0409'],[bal:'credit',dataRequest:'glAllBranch'])-0)/12*9+                //问题---0代表，最低贷方余额
            sumItems(['0408','0409'],[dataRequest:'cfLiquid'])
        }
    }
    apply sel([rowdim:32,lpid:10074]),{
        formula{
            (sumItemsWithMinus0(['S2017'],['1404','0408','0409'],[bal:'credit',dataRequest:'glAllBranch']))+         
            sumItems(['0408','0409'],[dataRequest:'cfLiquid'])
        }
    }
    //3.3同业拆入

	//次日
	apply buildSelector([rowdim:33,lpid:10069]), {
		formula {v0([item:"S2019",bal:'credit',dataRequest:'glAllBranch'])-colRel(1)-colRel(2)-colRel(3)-colRel(4)-colRel(5)}
	}
	
	//2-7日 to 一年以上
	apply buildSelector([rowdim:33,lpid:10070..10074]), {
        params ([item:'S2019',dataRequest:'cfLiquid'])
	}
	
    //3.4卖出回购款项（不含非金融机构）（数据待确定）

    //3.5各项存款
	//次日 (2014活期的未处理)
	apply buildSelector([rowdim:35,lpid:10069]), {
		formula {rowRel(1)+rowRel(2)+sumItems0(['S2011','S2012'],[bal:'credit',dataRequest:'glAllBranch'])+v0([item:"S2014",bal:'credit',dataRequest:'glAllBranch'])}
	}
	
	//2-7日..1年以上
	apply buildSelector([rowdim:35,lpid:10070..10074]), {
		formula {rowRel(1)}
	}
	
    //3.5.1其中：定期存款（不含财政性存款）

	//次日(定期的2014未处理)   to  一年以上
	apply buildSelector([rowdim:351,lpid:10069..10074]), {
		formula {sumItems(['S2002','S2004','S2014'],[dataRequest:'cfLiquid'])}
	}
	
	
    //3.5.2活期存款（不含财政性存款）()
	apply buildSelector([rowdim:352,lpid:10069]), {
		formula {sumItems0(['S2001','S2003','S2005'],[bal:'credit',dataRequest:'glAllBranch'])}
	}
	
    //3.6发行债券（待确定数据）
    //次日 to 一年以上
	apply buildSelector([rowdim:36,lpid:10069..10074]), {
		params ([item:'S2502',dataRequest:'cfLiquid'])
	}
	
    //3.7其他有确定到期日的负债

	//次日   
	apply buildSelector([rowdim:37,lpid:10069]), {
		formula {sumItems0(['S2007','S2013','S2101'],[bal:'credit',dataRequest:'glAllBranch'])+sumItems0(['0450','1451','3002'],[bal:'credit',dataRequest:'glAllBranch'])+sumItems(['S2231','S2701'],[dataRequest:'cfLiquid'])+mathPositive(v0([item:"S2006",bal:'credit',dataRequest:'glAllBranch']),v0([item:"0013",bal:'debit',dataRequest:'glAllBranch']),0.00)+mathPositive(v0([item:"S3031",bal:'credit',dataRequest:'glAllBranch']),v0([item:"S3031",bal:'debit',dataRequest:'glAllBranch']),0.00)+mathPositive(v0([item:"S30411",bal:'credit',dataRequest:'glAllBranch']),v0([item:"S30411",bal:'debit',dataRequest:'glAllBranch']),0.00)+mathPositive(v0([item:"S30412",bal:'credit',dataRequest:'glAllBranch']),v0([item:"S30412",bal:'debit',dataRequest:'glAllBranch']),0.00)+mathPositive(v0([item:"S30413",bal:'credit',dataRequest:'glAllBranch']),v0([item:"S30413",bal:'debit',dataRequest:'glAllBranch']),0.00)+mathPositive(v0([item:"S30414",bal:'credit',dataRequest:'glAllBranch']),v0([item:"S30414",bal:'debit',dataRequest:'glAllBranch']),0.00)+mathPositive(v0([item:"S30415",bal:'credit',dataRequest:'glAllBranch']),v0([item:"S30415",bal:'debit',dataRequest:'glAllBranch']),0.00)+mathPositive(v0([item:"S30416",bal:'credit',dataRequest:'glAllBranch']),v0([item:"S30416",bal:'debit',dataRequest:'glAllBranch']),0.00)+mathPositive(v0([item:"S30417",bal:'credit',dataRequest:'glAllBranch']),v0([item:"S30417",bal:'debit',dataRequest:'glAllBranch']),0.00)+mathPositive(v0([item:"S30419",bal:'credit',dataRequest:'glAllBranch']),v0([item:"S30419",bal:'debit',dataRequest:'glAllBranch']),0.00)+mathPositive(v0([item:"S3051",bal:'credit',dataRequest:'glAllBranch']),v0([item:"S3051",bal:'debit',dataRequest:'glAllBranch']),0.00)+mathPositive(v0([item:"S3301",bal:'credit',dataRequest:'glAllBranch']),v0([item:"S3301",bal:'debit',dataRequest:'glAllBranch']),0.00)}
	}
	
	//2-7日 to 一年以上
	apply buildSelector([rowdim:37,lpid:10070..10074]), {
		formula {sumItems(['S2231','S2701'],[dataRequest:'cfLiquid'])}
	}
	

    //3.8没有确定到期日的负债（未定期限）
	apply buildSelector([rowdim:38,lpid:1]), {
		formula {sumItems0(['S2015','S2021','S2022','S2211','S2221','S2232','S2312','S2701','S2702','S2711','S2801','S2901'],[bal:'credit',dataRequest:'glAllBranch'])+
                v0([item:"S2241",bal:'credit',dataRequest:'glAllBranch'])-
                sumItems0(['0450','1451','3002'],[bal:'credit',dataRequest:'glAllBranch'])+
                mathPositive(v0([item:"S2313",bal:'credit',dataRequest:'glAllBranch']),v0([item:"S1311",bal:'debit',dataRequest:'glAllBranch']),0.00)+
                mathPositive(v0([item:"S2314",bal:'credit',dataRequest:'glAllBranch']),v0([item:"S1321",bal:'debit',dataRequest:'glAllBranch']),0.00)}	
	}
	
    //5.到期期限缺口
	apply buildSelector([rowdim:5,lpid:10069..10074]), {
		formula {rowRel(-27)+rowRel(-17)-rowRel(-14)-rowRel(-3)}
	}

    //6.累计到期期限缺口
	apply buildSelector([rowdim:6,lpid:10069]), {
		formula {rowRel(-1)}
	}
	
	apply buildSelector([rowdim:6,lpid:10070..10074]), {
		formula {colRel(-1)+rowRel(-1)}
	}
	
    //7.附注：活期存款（不含财政性存款）

	//次日
	apply buildSelector([rowdim:7,lpid:10069]), {
		formula {(sumItems0(['S2001','S2003','S2005'],[bal:'credit',dataRequest:'glAllBranch'])-colRel(5))/360}
	}
	
	//2-7日
	apply buildSelector([rowdim:7,lpid:10070]), {
		formula {(sumItems0(['S2001','S2003','S2005'],[bal:'credit',dataRequest:'glAllBranch'])-colRel(4))/360*6}
	}
	
	//8-30日
	apply buildSelector([rowdim:7,lpid:10071]), {
		formula {(sumItems0(['S2001','S2003','S2005'],[bal:'credit',dataRequest:'glAllBranch'])-colRel(3))/360*23}
	}
	
	//31-90日
	apply buildSelector([rowdim:7,lpid:10072]), {
		formula {(sumItems0(['S2001','S2003','S2005'],[bal:'credit',dataRequest:'glAllBranch'])-colRel(2))/12*2}
	}
	
	//91日-1年
	apply buildSelector([rowdim:7,lpid:10073]), {
		formula {(sumItems0(['S2001','S2003','S2005'],[bal:'credit',dataRequest:'glAllBranch'])-colRel(1))/12*9}
	}
	
	//1年以上（待数据确定）
	apply buildSelector([rowdim:7,lpid:10074]), {
		formula {0.00}
	}
	

	
		
}

