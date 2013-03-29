package net.jyreport.core.support

import net.jyreport.core.*

/**
 *
 */
@Category(Number)
class NumberCategory {
	
	
	Formula toFormula(){
		new Formula(this)
	}
	
	Number getResult(){
		this
	}
	
	// +
	
	Formula plus(Formula formula){
		if(formula.evaluated){
			new Formula(this + formula.result)
		}else{
			new Formula({this + formula.result})
		}
	}
	
	// -
	
	Formula minus(Formula formula){
		if(formula.evaluated){
			new Formula(this - formula.result)
		}else{
			new Formula({this - formula.result})
		}
	}
	
	// *
	
	Formula multiply(Formula formula){
		if(formula.evaluated){
			new Formula(this * formula.result)
		}else{
			new Formula({this * formula.result})
		}
	}
	
	// /
	
	Formula div(Formula formula){
		if(formula.evaluated){
			def divResult=0.0d
			def formulaResult=formula.result
			if(formulaResult){
				divResult=this / formulaResult
			}else{
				println "divided by zero!"
			}
			new Formula(divResult)
		}else{
			def divCode={
				def formulaResult=formula.result
				if(formulaResult){
					this / formulaResult
				}else{
					println "divided by zero!"
					0.0d
				}
			}
			new Formula(divCode)
		}
	}
	
	// power
	
	Formula power(Formula formula){
		if(formula.evaluated){
			new Formula(this.power(formula.result))
		}else{
			new Formula({this.power(formula.result)})
		}
	}
	
	// %
	
	Formula mod(Formula another){
		if(another.evaluated){
			new Formula(this % another.result)
		}else{
			new Formula({this % another.result})
		}
	}
}

