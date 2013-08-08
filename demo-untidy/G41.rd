code 'G41'
name '利率重新定价风险情况表'
dimensions{
    rowdim G51Demension
    rpid RepriceRateDimension
}

layout{
    rows 'rowdim'
    columns 'rpid'
}

dataRequest {
    table 'gl'
    fields 'item,currency,sum(endingBalance) endingBalance,sum(debitBalance) debitBalance,sum(creditBalance) creditBalance'
    groupFields 'item,currency'
    contextParams (['date','currency'])
}

dataRequest{
    table 'glitem'
    fields 'item,currency,sum(endingBalance) endingBalance,sum(debitBalance) debitBalance,sum(creditBalance) creditBalance'
	groupFields 'item,currency'
	params {
		date 'context[]'
		currency 'context[]'
	}
}

dataRequest{
        table 'reRate'
        fields 'item,balance,rpid'
        params {
            date 'context[]'
            currency 'context[]'
            staticModel 'context[]'
        }
}

dataGrids{
    //虚拟  初始化 每一个格子的数据都为 111111111
    //apply sel([null,null]),{
      //  formula{
      //      111111111
       // }
    //}
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
        dataType new TextType()
    }
    apply sel([31,1]),{
        formula{1.917}
    }
    apply sel([31,2]),{
        formula{1.667}
    }
    apply sel([31,3]),{
        formula{1.250}
    }
    apply sel([31,4]),{
        formula{0.500}
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
        dataType new TextType()
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

    apply sel([36,0]),{
        formula{
                rr(-1)/rr(1)
        }
    }
    apply sel([rowdim:38,rpid:1]),{
        formula{
            sumItems0([4001,4002,4101,4102,4103,4104,1309,2502],[bal:'credit',dataRequest:'glitem'])+
            0- //损益类钆差后贷方余额
            0-  //转向准备
            (
                 //手工维护
                v0([dict:'xttz',dataRequest:'manualData'])+ //仙桃
                v0([dict:'jmtz',dataRequest:'manualData'])+  //即墨
                v0([dict:'nxytz',dataRequest:'manualData'])+   //农信

                v0([item:"1521",bal:'debit',dataRequest:'glitem'])-
                v0([item:"1521",bal:'credit',dataRequest:'glitem'])
            )
        }
    }
}
