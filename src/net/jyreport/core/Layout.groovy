package net.jyreport.core

/**
 *
 */
class Layout implements Serializable,Cloneable {
	
	List<Dimension> rds
	List<Dimension> cds
	List<String> rowDimensions
	List<String> colDimensions
	
	void setRd(ds){
		rds=[]
		rowDimensions=[]
		setDims(rowDimensions,rds,ds)
	}
	
	void setCd(ds){
		cds=[]
		colDimensions=[]
		setDims(colDimensions,cds,ds)
	}
	
	protected setDims(dim,dd,ds){
		if(ds instanceof String){
			ds=[ds]
		}
		(ds as List).each{
			if(it instanceof Class){
				def d=it.newInstance()
				dd << d
				dim << d.name
			}else if(it instanceof Dimension){
				dd << it
				dim << it.name
			}else if(it instanceof String){
				//TODO:
				dim << it
			}else{
				println "dim: $it"
			}
		}
	}
	
	String toString(){
		"$rowDimensions :: $colDimensions"
	}
}

