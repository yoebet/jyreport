code 'G23'
name '重点客户监测表'
dimensions{
    rowdim Dimension,{
            modelClass HashMap
            name 'seqNo'
            text '序号'
            parentIdProperty 'parent'
            valueProperty 'id'
            hierarchical true
            headModels([    
                [id:1,name:'最大单一存款客户'],
                [id:2,name:'最大单一贷款客户'],
                [id:3,name:'最大十家存款客户'],
                [id:30,name:'1',parent:3],
                [id:31,name:'2',parent:3],
                [id:32,name:'3',parent:3],
                [id:33,name:'4',parent:3],
                [id:34,name:'5',parent:3],
                [id:35,name:'6',parent:3],
                [id:36,name:'7',parent:3],
                [id:37,name:'8',parent:3],
                [id:38,name:'9',parent:3],
                [id:39,name:'10',parent:3],

                [id:4,name:'最大十家贷款客户'],
                [id:40,name:'1',parent:4],
                [id:41,name:'2',parent:4],
                [id:42,name:'3',parent:4],
                [id:43,name:'4',parent:4],
                [id:44,name:'5',parent:4],
                [id:45,name:'6',parent:4],
                [id:46,name:'7',parent:4],
                [id:47,name:'8',parent:4],
                [id:48,name:'9',parent:4],
                [id:49,name:'10',parent:4],


                [id:5,name:'最大十家同业融入'],
                [id:50,name:'1',parent:5],
                [id:51,name:'2',parent:5],
                [id:52,name:'3',parent:5],
                [id:53,name:'4',parent:5],
                [id:54,name:'5',parent:5],
                [id:55,name:'6',parent:5],
                [id:56,name:'7',parent:5],
                [id:57,name:'8',parent:5],
                [id:58,name:'9',parent:5],
                [id:59,name:'10',parent:5],

                [id:6,name:'最大十家同业融出'],
                [id:60,name:'1',parent:6],
                [id:61,name:'2',parent:6],
                [id:62,name:'3',parent:6],
                [id:63,name:'4',parent:6],
                [id:64,name:'5',parent:6],
                [id:65,name:'6',parent:6],
                [id:66,name:'7',parent:6],
                [id:67,name:'8',parent:6],
                [id:68,name:'9',parent:6],
                [id:69,name:'10',parent:6],

            ])
    }

    coldim Dimension,{
            name 'monitor'
            text '重点客户监测'
            modelClass HashMap
            headModels([
                [id:'cusNum',name:'客户号'],
                [id:'accNum',name:'账号'],
                [id:'cusName',name:'户名'],
                [id:'balance',name:'余额']
            ])
    }
}

layout {
	rows 'seqNo'
	columns 'monitor'
}

dataRequest {
    table 'monitor'
    params {
         date cw({Helper.parseParamDate(context['date'])})
    }    
}

dataGrids {
    apply selectColumns(0..2), {
        dataType new TextType()
    }
    apply sel([0,0..3]),{
       formula { rowRel(3)}
    }
    apply sel([1,0..3]),{
       formula { rowRel(13)}
    }
    apply selectRows([2,13,24,35]), {
        dataType new TextType()
    }
}