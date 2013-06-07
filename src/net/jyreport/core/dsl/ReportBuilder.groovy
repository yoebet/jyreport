package net.jyreport.core.dsl

import net.jyreport.core.dimension.*
import net.jyreport.core.selector.*
import net.jyreport.core.grid.*
import net.jyreport.core.*

import static net.jyreport.core.Selector.*

class ReportBuilder extends BaseBuilder {
	
	Report report
	
	String currentDimension
	
	def combinedSelectorBuilder
	
	Selector selector
	
	DataGrid templateGrid
	
	Map<String,String> getNameMapping(){
		[
			freq:'frequency',
			rows:'rowDimensions',
			cols:'colDimensions',
			columns:'colDimensions',
			data:'dataRequests',
			heads:'headCriterias'
		]
	}
	
	ReportBuilder(){
		if(nameRegistrar==null){
			nameRegistrar=new NameRegistrar()
		}
	}
	
	ReportBuilder(NameRegistrar nameRegistrar){
		this.nameRegistrar=nameRegistrar
	}
	
	
	//current:
	//dimensions,dataRequests,headCriterias,derivedHeads,dataGrids
	//dataRequest,applyGrids,selectorBuilder,selector

	protected void setClosureDelegate(Closure closure, Object node) {
		def del
		switch(node){
			case Dimension:
			del=new DimensionBuilder(node)
			break
			case DataRequest:
			del=new DataRequestBuilder(node)
			break
			case DataCriteria:
			del=new DataCriteriaBuilder(node)
			break
			case Head:
			del=new HeadBuilder(node)
			break
			case Grid:
			del=new GridBuilder(node)
			break
			case Selector:
			del=new SelectorBuilder(node)
			break
			case CombinedSelectorBuilder:
			del=node
			break
			default:
			del=this
		}
		del.nameRegistrar=this.nameRegistrar
		closure.resolveStrategy=Closure.DELEGATE_FIRST
		closure.setDelegate(del)
	}
	
	Map dimensionAlias=[:]
	
	protected Object doCreateNode(Object name, Map attributes, Object value){
		def node=name
		switch(current){
			case null:
			if(name=='call'){
				if(value==null){
					if(report==null){
						report=new Report()
					}
				}else if(value instanceof Class){
					report=value.newInstance()
				}else if(value instanceof Report){
					report=value
				}else{
					logUnknownValue(name,value)
				}
				if(report!=null && attributes!=null){
					setProperties(report,attributes)
				}
				node=report
			}
			break
			case report:
			switch(name){
				case 'layout':
				if(value==null){
					report.layout=new Layout()
				}else if(value instanceof Layout){
					report.layout=value
				}else{
					logUnknownValue(name,value)
					break
				}
				if(attributes!=null){
					setProperties(report.layout,attributes)
				}
				break
				case 'dataType':
				report.dataType=parseDataType(value,attributes)
				break
				case 'display':
				if(value==null){
					report.display=new Display()
				}else if(value instanceof Display){
					report.display=value
				}else if(value instanceof Class){
					report.display=value.newInstance()
				}else{
					logUnknownValue(name,value)
					break
				}
				if(attributes!=null){
					setProperties(report.display,attributes)
				}
				break
				case 'dataRequest':
				if(report.dataRequests==null){
					report.dataRequests=[]
				}
				def dataRequest
				if(value==null){
					dataRequest=new DataRequest()
				}else if(value instanceof DataRequest){
					dataRequest=value
				}else if(value instanceof Class){
					dataRequest=value.newInstance()
				}else if(value instanceof String){
					dataRequest=new DataRequest(name:value)
				}else{
					logUnknownName(name,value)
					break
				}
				if(attributes!=null){
					setProperties(dataRequest,attributes)
				}
				report.dataRequests << dataRequest
				node=dataRequest
				break
				case ['dimensions','dataRequests']:
				report[name]=(value!=null)? value : []
				if(attributes!=null){
					logSnha(name,attributes)
				}
				break
				case ['headCriterias','derivedHeads','dataGrids']:
				if(attributes!=null){
					report[name]=attributes
				}else{
					report[name]=[:]
				}
				if(value!=null){
					logUnknownValue(name,value)
				}
				break
				default:
				if(report.hasProperty(name)){
					report[name]=value
				}else{
					logUnknownName(name,value)
				}
			}
			break
			case 'layout':
			if(['rowDimensions','colDimensions'].contains(name) && value instanceof String){
				report.layout[name]=[value]
			}else{
				setProperty(report.layout,name,value,null)
			}
			break
			case 'dataType':
			setProperty(report.dataType,name,value,attributes)
			break
			case 'display':
			setProperty(report.display,name,value,null)
			break
			case 'dimensions':
			def dimension
			if(value==null){
				def registrarClass=nameRegistrar.dimensionClasses[name]
				if(registrarClass){
					dimension=registrarClass.newInstance()
					if(dimension.name!=name){
						dimensionAlias[name]=dimension.name
					}
				}else{
					dimension=new Dimension()
					dimension.name=name
				}
			}else if(value instanceof Dimension){
				dimension=value
				if(dimension.name==null){
					dimension.name=name
				}
			}else if(value instanceof Class){
				dimension=value.newInstance()
				if(dimension.name==null){
					dimension.name=name
				}
			}else{
				logUnknownValue(name,value)
				break
			}
			if(attributes!=null){
				setProperties(dimension,attributes)
			}
			report.dimensions << dimension
			node=dimension
			break
			case 'dataRequests':
			def dataRequest
			if(value==null){
				dataRequest=new DataRequest()
			}else if(value instanceof DataRequest){
				dataRequest=value
			}else if(value instanceof Class){
				dataRequest=value.newInstance()
			}else{
				logUnknownValue(name,value)
				break
			}
			if(attributes!=null){
				setProperties(dataRequest,attributes)
			}
			dataRequest.name=name
			report.dataRequests << dataRequest
			node=dataRequest
			break
			case 'headCriterias':
			def headCriteria
			if(value==null){
				headCriteria=new DataCriteria()
			}else if(value instanceof DataCriteria){
				headCriteria=value
			}else if(value instanceof Class){
				headCriteria=value.newInstance()
			}else{
				logUnknownValue(name,value)
				break
			}
			if(attributes!=null){
				setProperties(headCriteria,attributes)
			}
			currentDimension=dimensionAlias[name] ?: name
			report.headCriterias[currentDimension]=headCriteria
			node=headCriteria
			break
			case 'derivedHeads':
			def heads
			if(value==null){
				heads=[]
			}else if(value instanceof List){
				heads=value
			}else{
				logUnknownValue(name,value)
				break
			}
			currentDimension=dimensionAlias[name] ?: name
			report.derivedHeads[currentDimension]=heads
			node='derivedHeadList'
			break
			case 'derivedHeadList':
			if(name!='head'){
				logUnknownName(name,value)
				break
			}
			def head
			if(value==null){
				head=new Head()
			}else if(value instanceof Head){
				head=value
			}else if(value instanceof Class){
				head=value.newInstance()
			}else{
				logUnknownValue(name,value)
				break
			}
			if(attributes!=null){
				setProperties(head,attributes)
			}
			report.derivedHeads[currentDimension] << head
			node=head
			break
			case 'dataGrids':
			if(name!='apply'){
				logUnknownName(name,value)
				break
			}
			if(value==null){
				node='applyGrids'
				break
			}
			def selector
			if(value==null && attributes!=null){
				selector=buildSelector(attributes)
			}else{
				selector=buildSelector(value)
				setProperties(selector,attributes)
			}
			def dataGrid=new DataGrid()
			report.dataGrids[selector]=dataGrid
			node=dataGrid
			break
			case 'applyGrids':
			switch(name){
				case 'select':
				if(value==null && attributes!=null){
					selector=buildSelector(attributes)
				}else{
					selector=buildSelector(value)
					setProperties(selector,attributes)
				}
				node=selector
				break
				case ['and','or','not']:
				if(name=='not' && value!=null){
					selector=buildSelector(value)
					if(attributes!=null){
						setProperties(selector,attributes)
					}
					node=selector
					selector=new NotSelector(selector)
					break
				}
				if(value!=null){
					logUnknownValue(name,value)
				}
				if(attributes!=null){
					logSnha name,attributes
				}
				combinedSelectorBuilder=new CombinedSelectorBuilder(name)
				node=combinedSelectorBuilder
				break
				case 'template':
				if(value==null){
					templateGrid=new DataGrid()
				}else if(value instanceof DataGrid){
					templateGrid=value
				}else if(value instanceof Class){
					templateGrid=value.newInstance()
				}else{
					logUnknownValue(name,value)
					break
				}
				if(attributes!=null){
					setProperties(templateGrid,attributes)
				}
				node=templateGrid
				break
				default:
				logUnknownName(name,value)
			}
			break
			default:
			logUnknownName(name,value)
		}
		node
	}
	
	protected void nodeCompleted(Object parent, Object node) {
		if(node==combinedSelectorBuilder){
			selector=combinedSelectorBuilder.result
		}else if(node=='applyGrids'){
			report.dataGrids[selector]=templateGrid
		}
	}
}

