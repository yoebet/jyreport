
import net.jyreport.core.*
import net.jyreport.core.grid.*
import net.jyreport.core.support.*
import net.jyreport.core.datatype.*
import net.jyreport.core.selector.*
import net.jyreport.core.dataprovider.*
import net.jyreport.core.headprocessor.*
import net.jyreport.core.resultbuilder.*
import net.jyreport.core.instrument.*

import net.jyreport.demo.*
import net.jyreport.demo.grid.*
import net.jyreport.demo.dimension.*
import net.jyreport.demo.datatable.*
import net.jyreport.demo.headprocessor.*
import net.jyreport.demo.datatable.staticmodel.*
import net.jyreport.demo.datatable.scenemodel.*
import net.jyreport.demo.datarequest.*
import net.jyreport.demo.dsl.*

import static net.jyreport.core.Head.*
import static net.jyreport.core.Selector.*
import static net.jyreport.core.grid.DataGrid.*
import static net.jyreport.core.support.Formula.*
import static net.jyreport.core.dsl.ClosureWrapper.*



def builder = new ReportBuilder()
builder.call {

$content

}
