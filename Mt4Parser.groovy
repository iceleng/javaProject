
class Transaction{
	int ticket
	Date openTime,closeTime
	String type,item
	BigDecimal size,orderPrice,sl,tp,dealPrice,swap,profit

	@Override
	public String toString(){
		"${ticket}\t${openTime}\t${closeTime}\t${type}\t${item}\t${size}\t${orderPrice}\t${sl}\t${tp}\t${dealPrice}\t${swap}\t${profit}"
	}
}

import org.apache.log4j.Logger
import com.googlecode.groovyhttp.Http
import net.htmlparser.jericho.Source
import net.htmlparser.jericho.HTMLElementName

log = Logger.getLogger("Mt4Parser")
def file=new File("20160216_DetailedStatement.htm")
def source=new Source(file)
/*
begin:第四个Tr开始是交易数据
end  :出现“Closed P/L:”字符的tr之前第二个TR结束交易数据
*/
def count=0
source.getAllElements(HTMLElementName.TR).each{tr->
	//printf "%5d\t%s\r\n",++count,it.getAllElements(HTMLElementName.TD).size()
	try{
		def firstTD=tr.getAllElements(HTMLElementName.TD)[0].getContent().getTextExtractor().toString() as long
		tr.getAllElements(HTMLElementName.TD).each{td->
			print "${td.getContent().getTextExtractor().toString()}\t"
		}
		println ""
	}catch(ex){
		//println ex
	}
}
