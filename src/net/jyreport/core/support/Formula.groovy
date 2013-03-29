package net.jyreport.core.support

import net.jyreport.core.*

/**
 *
 */
class Formula implements Comparable,Cloneable,Serializable {
	
	def value
	
	def evaluator
	
	boolean evaluated
	
	protected boolean evaluating
	
	def savedValue
	
	boolean savedEvaluated
	
	boolean usingCompareOperator
	
	Formula(){
	}
	
	Formula(expr){
		setValue(expr)
	}
	
	def tryEvaluateBegin(){
		savedValue=value
		savedEvaluated=evaluated
	}
	
	def tryEvaluateDone(){
		value=savedValue
		evaluated=savedEvaluated
	}
	
	Object evaluate(){
		if(evaluating){
			throw new RuntimeException("in evaluating: ${this}")
		}
		evaluating=true
		if(evaluator!=null){
			value=evaluator.call()
			while(value instanceof Formula){
				value=value.result
			}
			if(value instanceof BigInteger){
				value=value.longValue()
			}else if(value instanceof BigDecimal){
				value=value.doubleValue()
			}
		}
		evaluated=true
		evaluating=false
	}
	
	def getResult(){
		if(!evaluated){
			evaluate()
		}
		if(value==null){
			return 0
		}
		value
	}
	
	def reset(){
		evaluated=false
	}
	
	void setFormula(expr){
		setValue(expr)
	}
	
	void setValue(expr){
		if(expr instanceof Closure){
			evaluated=false
			evaluator=expr
			evaluator.delegate=this
		}else if(expr instanceof Formula){
			value=expr.value
			evaluated=expr.evaluated
			def exprEvaluator=expr.evaluator
			if(exprEvaluator){
				evaluator=exprEvaluator
				if(exprEvaluator.delegate==null){
					evaluator=evaluator.clone()
					evaluator.delegate=this
					evaluator.resolveStrategy=Closure.DELEGATE_FIRST
				}
			}
		}else{
			if(expr instanceof BigInteger){
				value=expr.longValue()
			}else if(expr instanceof BigDecimal){
				value=expr.doubleValue()
			}else{
				value=expr
			}
			evaluated=true
		}
	}
	
	// +
	
	Formula plus(Formula another){
		if(evaluated && another.evaluated){
			new Formula(result + another.result)
		}else{
			new Formula({result + another.result})
		}
	}
	
	Formula plus(Number number){
		if(evaluated){
			new Formula(result + number)
		}else{
			new Formula({result + number})
		}
	}
	
	// -
	
	Formula minus(Formula another){
		if(evaluated && another.evaluated){
			new Formula(result - another.result)
		}else{
			new Formula({result - another.result})
		}
	}
	
	Formula minus(Number number){
		if(evaluated){
			new Formula(result - number)
		}else{
			new Formula({result - number})
		}
	}
	
	// *
	
	Formula multiply(Formula another){
		if(evaluated && another.evaluated){
			new Formula(result * another.result)
		}else{
			new Formula({result * another.result})
		}
	}
	
	Formula multiply(Number number){
		if(evaluated){
			new Formula(result * number)
		}else{
			new Formula({result * number})
		}
	}
	
	// /
	
	Formula div(Formula another){
		if(evaluated && another.evaluated){
			def divResult=0.0d
			def anotherResult=another.result
			if(anotherResult){
				divResult=result / anotherResult
			}else{
				println "divided by zero!"
			}
			new Formula(divResult)
		}else{
			def divCode={
				def anotherResult=another.result
				if(anotherResult){
					result / anotherResult
				}else{
					println "divided by zero!"
					0.0d
				}
			}
			new Formula(divCode)
		}
	}
	
	Formula div(Number number){
		if(evaluated){
			def divResult=0.0d
			if(number){
				divResult=result / number
			}else{
				println "divided by zero!"
			}
			new Formula(divResult)
		}else{
			def divCode={->
				if(number){
					result / number
				}else{
					println "divided by zero!"
					0.0d
				}
			}
			new Formula(divCode)
		}
	}
	
	// -
	
	Formula negative(){
		if(evaluated){
			new Formula(- result)
		}else{
			new Formula({- result})
		}
	}
	
	// power
	
	Formula power(Formula another){
		if(evaluated && another.evaluated){
			new Formula(result.power(another.result))
		}else{
			new Formula({result.power(another.result)})
		}
	}
	
	Formula power(Number number){
		if(evaluated){
			new Formula(result.power(number))
		}else{
			new Formula({result.power(number)})
		}
	}
	
	// %
	
	Formula mod(Formula another){
		if(evaluated && another.evaluated){
			new Formula(result % another.result)
		}else{
			new Formula({result % another.result})
		}
	}
	
	Formula mod(Number number){
		if(evaluated){
			new Formula(result % number)
		}else{
			new Formula({result % number})
		}
	}
	
	// abs
	
	Formula abs(){
		if(evaluated){
			new Formula(result.abs())
		}else{
			new Formula({result.abs()})
		}
	}
	
	/****************************/
	
	Object asType(Class clazz){
		if(clazz==Closure){
			if(evaluator){
				return evaluator
			}else{
				return {value}
			}
		}
		return result?.asType(clazz)
	}
	
	void useCompareOperator(Closure c){
		boolean savedUsingCompareOperator=usingCompareOperator
		usingCompareOperator=true
		c.call()
		usingCompareOperator=savedUsingCompareOperator
	}
	
	int compareTo(Object another){
		if(another==null){
			return 1
		}
		if(!usingCompareOperator){
			return -1
		}
		if(another instanceof Number){
			if(result==null){
				return (another==null)? 0:-1
			}
			return result <=> another
		}
		if(result==null){
			return (another.result==null)? 0:-1
		}
		return result <=> another.result
	}
	
	boolean equals(Object another){
		if(!usingCompareOperator){
			reture this.is(another)
		}
		if(another instanceof Number){
			if(result==null){
				return another==null
			}
			return result == another
		}
		return result == another.result
	}
	
	/**********************************/
	
	static def SUM(Object[] formulas){
		formulas.sum()
	}
	
	static def MIN(Object[] formulas){
		new Formula({formulas.min()})
	}
	
	static def MAX(Object[] formulas){
		new Formula({formulas.max()})
	}
	
	static def AVG(Object[] formulas){
		if(formulas.size()>0){
			new Formula({formulas.sum()/formulas.size()})
		}else{
			new Formula(0.0d)
		}
	}
	
	static def ABS(formula){
		formula.abs()
	}
	
	static def IF(boolExpr,trueFormula,falseFormula){
		if(boolExpr instanceof Closure){
			boolExpr=new Formula(boolExpr)
		}
		new Formula({(boolExpr as Boolean)? trueFormula:falseFormula})
	}
}

