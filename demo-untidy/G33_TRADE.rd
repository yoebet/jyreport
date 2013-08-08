code 'G33_TRADE'
name '利率重新定价风险情况表(交易账户)'
dimensions{
    rowdim G51Demension
    rpid RepriceRateDimension
}

layout{
    rows 'rowdim'
    columns 'rpid'
}

dataRequest{
    table 'glAllBranch'
    fields 'item,currency,sum(endingBalance) endingBalance,sum(debitBalance) debitBalance,sum(creditBalance) creditBalance'
	groupFields 'item,currency'
	params {
		date 'context[]'
	}
}

dataRequest{
        table 'repriceRate'
        fields 'item,balance,rpid,currency'
        contextParams (['date','staticModel'])
}

dataRequest ManualDataRequest
dataRequest ErateDataRequest
dataGrids{
    apply selectAll(),{
        aggregator {curvs ->
            currencyExchange2(curvs,'01',true)
        }
        formula{0.000}
    }
    apply sel([0,null]),{
        formula{
            (1..4).sum({rr(it)})
        }
    }
    
    apply sel([1..4,0]),{
        formula{
            (1..13).sum({cr(it)})
        }
    }

//手工维护
apply sel([rowdim:4,rpid:10000]),{
        formula{
             v0([dict:'zqtz_trade_m1',dataRequest:'manualData']) 
        }
    }
apply sel([rowdim:4,rpid:10001]),{
        formula{
             v0([dict:'zqtz_trade_m2',dataRequest:'manualData']) 
        }
    }
apply sel([rowdim:4,rpid:10002]),{
        formula{
             v0([dict:'zqtz_trade_m3',dataRequest:'manualData']) 
        }
    }
apply sel([rowdim:4,rpid:10003]),{
        formula{
             v0([dict:'zqtz_trade_m4',dataRequest:'manualData']) 
        }
    }
apply sel([rowdim:4,rpid:10004]),{
        formula{
             v0([dict:'zqtz_trade_m5',dataRequest:'manualData']) 
        }
    }
apply sel([rowdim:4,rpid:10005]),{
        formula{
             v0([dict:'zqtz_trade_m6',dataRequest:'manualData']) 
        }
    }
apply sel([rowdim:4,rpid:10006]),{
        formula{
             v0([dict:'zqtz_trade_m7',dataRequest:'manualData']) 
        }
    }
apply sel([rowdim:4,rpid:10007]),{
        formula{
             v0([dict:'zqtz_trade_m8',dataRequest:'manualData']) 
        }
    }
apply sel([rowdim:4,rpid:10008]),{
        formula{
             v0([dict:'zqtz_trade_m9',dataRequest:'manualData']) 
        }
    }
apply sel([rowdim:4,rpid:10009]),{
        formula{
             v0([dict:'zqtz_trade_m10',dataRequest:'manualData']) 
        }
    }
apply sel([rowdim:4,rpid:10010]),{
        formula{
             v0([dict:'zqtz_trade_m11',dataRequest:'manualData']) 
        }
    }
apply sel([rowdim:4,rpid:10011]),{
        formula{
             v0([dict:'zqtz_trade_m12',dataRequest:'manualData']) 
        }
    }
apply sel([rowdim:4,rpid:10012]),{
        formula{
             v0([dict:'zqtz_trade_m13',dataRequest:'manualData']) 
        }
    }


//-----------
    apply sel([6,0]),{
        formula{
            rr(-1)+rr(-6)
        }
    }
    apply sel([7,null]),{
        formula{
            (1..5).sum({rr(it)})    
        }
    }
    apply sel([8..12,0]),{
        formula{
            (1..13).sum({cr(it)})
        }
    }
    apply sel([15,0]),{
        formula{
            rr(-1)+rr(-2)+rr(-8)    
        }
    }
    apply sel([16,1..-1]),{
        formula{
            rr(-9)-rr(-16)
        }
    }
    apply sel([17,1..-1]),{
        formula{
            [1,3,5,7,9,11].sum({rr(it)})-[2,4,6,8,10,12].sum({rr(it)})
        }
    }
    apply sel([18..29,0]),{
        formula{
             (1..13).sum({cr(it)})
        }
    }
    apply sel([30,0]),{
        formula{
            (1..13).sum({cr(it)})
        }
    }

    apply sel([30,1..-1]),{
        formula{
            rr(-13)+rr(-14)
        }
    }
    //设置单元格格式
    apply sel([31,null]),{
        dataType 'percent'
    }
    apply sel([31,1]),{
        formula{1.917/100}
    }
    apply sel([31,2]),{
        formula{1.667/100}
    }
    apply sel([31,3]),{
        formula{1.250/100}
    }
    apply sel([31,4]),{
        formula{0.500/100}
    }
    apply sel([32,0]),{
        formula{
            (1..4).sum({cr(it)})
        }
    }
    apply sel([32,1..4]),{
        formula{
            rr(-1)*rr(-2)
        }
    }
    apply sel([33,1..-1]),{
        formula{
            cr(-1)+rr(-3) 
        }
    }
    //设置单元格格式
    apply sel([31,null]),{
        dataType 'percent'
    }
    apply sel([34,1]),{
        formula{0.08}
    }
    apply sel([34,2]),{
        formula{0.32}
    }
    apply sel([34,3]),{
        formula{0.72}
    }
    apply sel([34,4]),{
        formula{1.43}
    }
    apply sel([34,5]),{
        formula{2.77}
    }
    apply sel([34,6]),{
        formula{4.49}
    }
    apply sel([34,7]),{
        formula{6.14}
    }
    apply sel([34,8]),{
        formula{7.711}
    }
    apply sel([34,9]),{
        formula{10.15}
    }
    apply sel([34,10]),{
        formula{13.26}
    }
    apply sel([34,11]),{
        formula{17.84}
    }
    apply sel([34,12]),{
        formula{22.43}
    }
    apply sel([34,13]),{
        formula{26.03}
    }
    apply sel([35,0]),{
        formula{
            (1..4).sum({cr(it)})
        }
    }
    apply sel([35,1..-1]),{
        formula{
            0-(rr(-5)*rr(-1))
        }
    }
    
    //设置这行的文本格式
    apply sel([rowdim:37,rpid:1]),{
        dataType 'percent'
    }
    apply sel([36,0]),{
        formula{
                rr(-1)/rr(1)
        }
    }
    apply sel([rowdim:38,rpid:1]),{
        formula{
          sumItems0(['S4001','S4002','S4101','S4102','S4103','S4104','S1309','S2502'],[bal:'credit',dataRequest:'glAllBranch'])+
          v0([dict:'syl',dataRequest:'manualData']) - //损益类钆差后贷方余额
          v0([dict:'zxzb',dataRequest:'manualData']) -  //专项准备
          (
                //手工维护
              v0([dict:'xttz',dataRequest:'manualData'])+ //仙桃
              v0([dict:'jmtz',dataRequest:'manualData'])+  //即墨
              v0([dict:'nxytz',dataRequest:'manualData'])+   //农信

              v0([item:"S1521",bal:'debit',dataRequest:'glAllBranch'])-
              v0([item:"S1521",bal:'credit',dataRequest:'glAllBranch'])
          )
        }
    }
}
