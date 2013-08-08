code 'G51'
name '利率重新定价风险情况表'
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
		currency 'context[]'
	}
}

dataRequest{
        table 'repriceRate'
        fields 'item,balance,rpid'
        contextParams (['date','currency','staticModel'])
}

dataGrids{
    
    apply sel([rowdim:1..5,rpid:1]),{
        formula {(1..13).sum({colRel(it)})}
    }
    apply sel([rowdim:1,rpid:10000..10012]),{
        formula {rowRel(1)+rowRel(2)}
    }
    apply sel(rowdim:2,rpid:10000),{
        formula {
            v0([item:"S1011",bal:'debit',dataRequest:'glAllBranch'])+
            v0([item:"S1012",bal:'debit',dataRequest:'glAllBranch'])+
            v0([item:"S1013",bal:'debit',dataRequest:'glAllBranch'])+
            v0([item:"S1014",bal:'debit',dataRequest:'glAllBranch'])+
            v0([item:"S1031",bal:'debit',dataRequest:'glAllBranch'])+
            v0([item:"S1111",bal:'debit',dataRequest:'glAllBranch'])-
            (1..6).sum({colRel(it)})
        }
    }
    apply sel([rowdim:2,rpid:10001..10012]),{
        formula {
            v([item:'S1111',dataRequest:'repriceRate'])+
            v([item:'S1011',dataRequest:'repriceRate'])+
            v([item:'S1013',dataRequest:'repriceRate'])
        }
    }
    apply sel([rowdim:3,rpid:10000]),{
        formula{
            v0([item:"S1038",bal:'debit',dataRequest:'glAllBranch'])+
            0+                 //逾期贷款glitem 有问题
            0+                 //正常货代reRate
            v([item:'S1307',dataRequest:'repriceRate'])
        }
    }
    apply sel([rowdim:3,rpid:10001..10012]),{
        formula{
            0+                 //正常货代  有问题
            v([item:'S1307',dataRequest:'repriceRate'])
        }
    }
    apply sel([rowdim:5,rpid:10000]),{
        formula{
            sumItemsWithMinus(['S1002','S1003'],['0013'],[bal:'debit',dataRequest:'glAllBranch'])+
            mathPositive(
                v0([item:"0013",bal:'debit',dataRequest:'glAllBranch']),
                v0([item:"2006",bal:'credit',dataRequest:'glAllBranch']),0
            )
    }
    }

    apply sel([rowdim:6,rpid:1]),{
        formula{
            sumItems0(['S1001','S1004','S1131','S1132','S1221','S1431','S1441','S1521','S1522','S1532','S1601','S1604','S1606','S1701','S1801','S1811','S1901'],[bal:'debit',dataRequest:'glAllBranch'])-
            sumItems0(['S2016','S1602','S1702'],[bal:'credit',dataRequest:'glAllBranch'])+
            v0([item:"0155",bal:'debit',dataRequest:'glAllBranch'])+
            v0([item:"0153",bal:'debit',dataRequest:'glAllBranch'])+
            //培训中心投资  手工维护            
            v0([dict:'pxzxkx',dataRequest:'manualData'])+         
            mathLarge(
                v0([item:"S1311",bal:'debit',dataRequest:'glAllBranch'])-
                v0([item:"S2313",bal:'credit',dataRequest:'glAllBranch']),0,0
            )+
            mathLarge(
                v0([item:"S1321",bal:'debit',dataRequest:'glAllBranch'])-
                v0([item:"S2314",bal:'credit',dataRequest:'glAllBranch']),0,0
            )+
            mathLarge(
                sumItems0(['S3031','S30411','S30412','S30413','S30414','S30415','S30416','S30417','S30419','S3051','S3301'],[bal:'credit',dataRequest:'glAllBranch'])
                ,0,0
            )
        }
    }
    apply sel([rowdim:7,rpid:1]),{
        formula{
             rowRel(-1)+rowRel(-6)
        }
    }
    apply sel([rowdim:8..13,rpid:1]),{
        formula{
            (1..13).sum({colRel(it)})
        }
    }
    apply sel([rowdim:8,rpid:10000..10012]),{
        formula{
            (1..5).sum({rowRel(it)})
        }
    }
    apply sel([rowdim:9,rpid:10000]),{
        formula{
            sumItems0(['S2017','S2018','S2019','S2020','S2021','S2022','S2101','S2111'],[bal:'credit',dataRequest:'glAllBranch'])-
            (1..12).sum({colRel(it)})-
             v0([item:"1404",bal:'credit',dataRequest:'glAllBranch'])
        }
    }
    apply sel([rowdim:9,rpid:10001..10012]),{
        formula{
            //v([item:"S2111",dataRequest:'repriceRate'])
            sumItemsWithMinus(['S2111','S2017','S2019'],['1404'],[dataRequest:'repriceRate'])
        }
    }
    apply sel([rowdim:10,rpid:10000]),{
        formula{
           sumItems0(['S2001','S2003','S2005','S2011','S2012','S2014'],[bal:'credit',dataRequest:'glAllBranch'])+
             v0([item:"1404",bal:'credit',dataRequest:'glAllBranch'])-
             v0([item:"1444",bal:'balance',dataRequest:'glAllBranch'])
        }       
    }
    apply sel([rowdim:11,rpid:10000..10012]),{
        formula{
            sumItems(['S2002','S2004','1444'],[dataRequest:'repriceRate'])
        }
    }
    apply sel([rowdim:12,rpid:10000..10012]),{
        formula{
            sumItems(['S2502'],[dataRequest:'repriceRate'])
        }
    }
    apply sel([rowdim:13,rpid:10000]),{
        formula{
            //sumItemsWithMinus0(['S2016','S2006'],['S1004','S0013'],[bal:'credit',dataRequest:'glAllBranch'])-
            //sumItems0(['S1004','S0013'],[bal:'debit',dataRequest:'glAllBranch'])+
            //sumItems(['S2501','S2015'],[dataRequest:'repriceRate'])
            mathPositive(v0([item:"S2016",bal:'credit',dataRequest:'glAllBranch']),v0([item:"1004",bal:'debit',dataRequest:'glAllBranch']),0)+
            mathPositive(v0([item:"S2006",bal:'credit',dataRequest:'glAllBranch']),v0([item:"0013",bal:'debit',dataRequest:'glAllBranch']),0)+
            sumItems(['S2501','S2015'],[dataRequest:'repriceRate'])
        }
    }
    apply sel([rowdim:13,rpid:10001..10012]),{
        formula{
            sumItems(['S2501','S2015'],[dataRequest:'repriceRate'])
        }
    }
    apply sel([rowdim:14,rpid:1]),{
        formula{
            sumItems0(['S1032','S1112','S1231','S1309','S1432','S1442','S1502','S1504','S1512','S1523','S1603','S1605','S1703','S2007','S2013','S2211','S2221','S2231','S2232','S2241','S2401','S2701','S2703','S2711','S2801','S2901'],[bal:'credit',dataRequest:'glAllBranch'])+
            //sumItemsWithMinus0(['S2313','S2314'],['S1311','S1321'],[bal:'credit',dataRequest:'glAllBranch'])+
            mathPositive(v0([item:"S2313",bal:'credit',dataRequest:'glAllBranch']),v0([item:"S1311",bal:'debit',dataRequest:'glAllBranch']),0)+
            mathPositive(v0([item:"S2314",bal:'credit',dataRequest:'glAllBranch']),v0([item:"S1321",bal:'debit',dataRequest:'glAllBranch']),0)+

            sumItems0(['S1311','S1321'],[bal:'credit',dataRequest:'glAllBranch'])+
            sumItems0(['S3031','S30411','S30412','S30413','S30414','S30415','S30416','S30417','S30419','S3051','S3301'],[bal:'credit',dataRequest:'glAllBranch'])
        }
    }
    apply sel([rowdim:15,rpid:1]),{
        formula{
            sumItems0(['S4001','S4002','S4101','S4102','S4103','S4104','S4201'],[bal:'credit',dataRequest:'glAllBranch'])
        }
    }
    apply sel([rowdim:16,rpid:1]),{
        formula{
           rowRel(-1)+rowRel(-2)+rowRel(-15)
        }
    }
    apply sel([rowdim:17,rpid:10000..10012]),{
        formula{
           rowRel(-16)-rowRel(-9)
        }
    }
    apply sel([rowdim:18,rpid:10000..10012]),{
        formula{
           rowRel(1)+rowRel(3)+rowRel(5)+rowRel(7)+rowRel(9)+rowRel(11)-
           rowRel(2)-rowRel(4)-rowRel(6)-rowRel(8)-rowRel(10)-rowRel(12)
        }
    }
    apply sel([rowdim:18..30,rpid:1]),{
        formula{
           (1..13).sum({colRel(it)})
        }
    }
    apply sel([rowdim:31,rpid:1]),{
        formula{
            (1..13).sum({colRel(it)})
        }
    }
    apply sel([rowdim:31,rpid:10000..10012]),{
        formula{
//           rowRel(-14)+rowRel(-1)    
            [-14,-1].sum({rowRel(it)})
        }
    }
 //设置这行的文本格式 百分比
    apply sel([rowdim:32,rpid:10000..10012]),{
       // dataType new PercentType()
        dataType 'percent'
    }
    apply sel([rowdim:32,rpid:10000]),{
        formula{
            1.917/100
        }
    }
    apply sel([rowdim:32,rpid:10001]),{
        formula{
           1.667/100
        }
    }
    apply sel([rowdim:32,rpid:10002]),{
        formula{
            1.250/100
        }
    }
    apply sel([rowdim:32,rpid:10003]),{
        formula{
            0.500/100
        }
    }
    apply sel([rowdim:33,rpid:1]),{
        formula{
            (1..4).sum({colRel(it)})
        }
    }
    apply sel([rowdim:33,rpid:10000..10003]),{
        formula{
           rowRel(-2)*rowRel(-1)
        }
    }
    apply sel([rowdim:34,rpid:10000..10006]),{
        formula{
           colRel(-1)+ rowRel(-3)
        }
    }
    apply sel([rowdim:34,rpid:10007]),{
        formula{
           colRel(-1)
        }
    }
    apply sel([rowdim:34,rpid:10008]),{
        formula{
           rel(-3,-8)+rel(-3,-7)+rel(-3,-6)+rel(-3,-5)+rel(-3,-4)+rel(-3,-3)+
           rel(-3,-2)+rel(-3,-1)+rel(-3,0)
        }
    }
    apply sel([rowdim:34,rpid:10009]),{
        formula{
           rel(-3,-9)+rel(-3,-8)+rel(-3,-7)+rel(-3,-6)+rel(-3,-5)+rel(-3,-4)+rel(-3,-3)+
           rel(-3,-2)+rel(-3,-1)+rel(-3,0)
        }
    }
    apply sel([rowdim:34,rpid:10010]),{
        formula{
           rel(-3,-10)+rel(-3,-9)+rel(-3,-8)+rel(-3,-7)+rel(-3,-6)+rel(-3,-5)+rel(-3,-4)+rel(-3,-3)+
           rel(-3,-2)+rel(-3,-1)+rel(-3,0)
        }
    }
    apply sel([rowdim:34,rpid:10011]),{
        formula{
           rel(-3,-11)+rel(-3,-10)+rel(-3,-9)+rel(-3,-8)+rel(-3,-7)+rel(-3,-6)+rel(-3,-5)+rel(-3,-4)+rel(-3,-3)+
           rel(-3,-2)+rel(-3,-1)+rel(-3,0)
        }
    }
    apply sel([rowdim:34,rpid:10012]),{
        formula{
           rel(-3,-12)+rel(-3,-11)+rel(-3,-10)+rel(-3,-9)+rel(-3,-8)+rel(-3,-7)+rel(-3,-6)+rel(-3,-5)+rel(-3,-4)+rel(-3,-3)+
           rel(-3,-2)+rel(-3,-1)+rel(-3,0)
        }
    }
     //设置这行的格式 百分比
    apply sel([rowdim:35,rpid:10000..10012]),{
        dataType 'percent'
    }
    apply sel([rowdim:35,rpid:10000]),{
        formula{
            0.08/100
        }
    }
    apply sel([rowdim:35,rpid:10001]),{
        formula{
                0.32/100
        }
    }
    apply sel([rowdim:35,rpid:10002]),{
        formula{
              0.72/100
        }
    }
    apply sel([rowdim:35,rpid:10003]),{
        formula{
            1.43/100
        }
    }
    apply sel([rowdim:35,rpid:10004]),{
        formula{
            2.77/100
        }
    }
    apply sel([rowdim:35,rpid:10005]),{
        formula{
            4.49/100
        }
    }
    apply sel([rowdim:35,rpid:10006]),{
        formula{
             6.14/100
        }
    }
    apply sel([rowdim:35,rpid:10007]),{
        formula{
                7.17/100
        }
    }
    apply sel([rowdim:35,rpid:10008]),{
        formula{
            10.15/100
        }
    }
    apply sel([rowdim:35,rpid:10009]),{
        formula{
            13.26/100
        }
    }
    apply sel([rowdim:35,rpid:10010]),{
        formula{
            17.84/100
        }
    }
    apply sel([rowdim:35,rpid:10011]),{
        formula{
            22.43/100
        }
    }
   
    apply sel([rowdim:35,rpid:10012]),{
        formula{
                26.03/100
        }
    }
    apply sel([rowdim:36,rpid:1]),{
        formula{
            (1..13).sum({colRel(it)})
        }
    }
    apply sel([rowdim:36,rpid:10000..10012]),{
        formula{
           -rowRel(-1)*rowRel(-5)
        }
    }
    //设置这行的文本格式
    apply sel([rowdim:37,rpid:1]),{
        dataType 'percent'
    }
    apply sel([rowdim:37,rpid:1]),{
        formula{
           rowRel(-1)/rowRel(1)
        }
    }
    
    apply sel([rowdim:38,rpid:1]),{
        formula{
          sumItems0(['S4001','S4002','S4101','S4102','S4103','S4104','S1309','S2502'],[bal:'credit',dataRequest:'glAllBranch'])+
          0- //损益类钆差后贷方余额
          0-  //专项准备
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
