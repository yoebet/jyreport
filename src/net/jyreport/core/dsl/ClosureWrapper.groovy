package net.jyreport.core.dsl

/**
 *
 */
class ClosureWrapper {
	
	Closure closure
	
	ClosureWrapper(Closure closure){
		this.closure=closure
	}
	
//	Object call(){
//		closure.call()
//	}
//	
//	Object asType(Class clazz){
//		if(clazz==Closure){
//			closure
//		}
//	}
//	
//	String toString(){
//		'{closure}'
//	}
	
	static ClosureWrapper cw(Closure closure){
		new ClosureWrapper(closure)
	}
}

