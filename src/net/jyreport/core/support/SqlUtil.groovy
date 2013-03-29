package net.jyreport.core.support

import java.sql.Date as SqlDate

/**
 *
 */
class SqlUtil {
	
	
	static def processParams(params,paramList,dimOrPropToField){
		def conditions=null
		def appendCondition={ cond ->
			if(conditions==null){
				conditions=new StringBuffer(' where')
			}else{
				conditions << ' and'
			}
			conditions << cond
		}
		
		params.each{ dimensionOrProperty,value ->
			def fieldName=dimOrPropToField(dimensionOrProperty)
			if(value==null){
				appendCondition(' '+fieldName + ' is null')
			}else if(value instanceof Collection){
				def op=value[0].toLowerCase()
				def opvalue=value[1]
				Set ops=['>','<','<>','=','>=','<=','like','not like']
				switch(op){
					case ops:
					appendCondition(' '+fieldName + ' '+ op +' ?')
					paramList << opvalue
					break
					case 'in':
					def vs=opvalue.size()
					if(vs==0){
						appendCondition(' 0 = 1')
					}else{
						def qms='?'+',?'*(vs-1)
						appendCondition(' '+fieldName + ' in ('+ qms +')')
						opvalue.each {paramList << it}
					}
					break
					case 'between':
					appendCondition(' '+fieldName + ' between ? and ?')
					paramList << opvalue[0]
					paramList << opvalue[1]
					break
					case 'not null':
					appendCondition(' '+fieldName + ' is not null')
					break
					default:
					println "unknown sql operator: $op"
				}
			}else{
				appendCondition(' '+fieldName + ' = ?')
				if(value instanceof Boolean){
					paramList << (value? 1:0)
				}else{
					paramList << value
				}
			}
		}
		
		return conditions?.toString()
	}
	
	static transParam(paramList){
		paramList.eachWithIndex{param,i->
			if(param instanceof Date && ! (param instanceof SqlDate)){
				paramList[i]=new SqlDate(param.time)
			}
		}
	}
	
	static processOrders(orders,dimOrPropToField){
		def orderString=null
		orders?.each{ orderBy ->
			if(orderString==null){
				orderString=new StringBuffer()
				orderString << ' order by '
			}else{
				orderString << ','
			}
			orderString << dimOrPropToField(orderBy[0])
			if(orderBy.size()>1){
				orderString << ' '+orderBy[1]
			}
		}
		return orderString
	}
}

