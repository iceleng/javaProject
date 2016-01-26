import static org.elasticsearch.node.NodeBuilder.*;
import org.elasticsearch.client.Client
import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.node.Node
//http://blog.csdn.net/july_2/article/details/25128179
//elasticsearch 为什么副本数多了 性能反而下降了
//http://zhidao.baidu.com/link?url=WKdhy-IbmWBEecTUhvBk2l_bG7l7HEKNiby42Pnm0zDW4xnnlGXpRgHWIV-SusAda-o1yk_kWaUElPqx_zDqwkSTNU5czHBU81BFXGR-Esi

//这个例子很好
//elasticsearch 口水篇（4）java客户端 - 原生esClient
//http://www.cnblogs.com/huangfox/p/3543134.html

//elasticsearch java api 获取索引(get)
//http://blog.mkfree.com/posts/516d1174975a2683ccfee2d0

//elasticsearch java api——客户端 org.elasticsearch.client
//http://blog.csdn.net/july_2/article/details/25128179

//elasticsearch Client 初始化方式以及清除数据方式
//http://www.cnblogs.com/soltex/p/3466714.html

//elasticsearch获取java client实例
//http://blog.csdn.net/july_2/article/details/44242857

//Java Clients for Elasticsearch
//https://www.elastic.co/blog/found-java-clients-for-elasticsearch

/*
// on startup
Properties p = System.getProperties();
p.put("es.path.home", "I:/elk/elasticsearch-2.1.1_node2");
//p.put("es.path.home", "I:/workspace/groovy/scanTongSe");
System.setProperties(p) 
Node node = nodeBuilder().clusterName("ice-elk").node();
//Node node = nodeBuilder().local(true).node();
//Node node = nodeBuilder().settings(Settings.settingsBuilder().put("http.enabled", false)).client(true).node();
Client client = node.client();
Thread.sleep(60000)
// on shutdown
node.close();
*/
import org.elasticsearch.common.transport.InetSocketTransportAddress
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.NoNodeAvailableException;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

Settings settings = Settings.settingsBuilder().put("cluster.name", "ice-elk").build();
//Add transport addresses and do something with the client...
Client client = TransportClient.builder().settings(settings).build()
        .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300))
//        .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("host2"), 9300));
//client.close();
// on shutdown

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.QueryBuilders.*;

//GetResponse response = client.prepareGet("movie", "tongse").get();
//使用ant生成javadoc文档
/*
<?xml version="1.0" encoding="UTF-8" ?>
<project name="sendNoticeMail" default="send-mail" basedir=".">
   <property name="src1" value="D:\elk\elasticsearch-master\core\src\main\java"/> 
   <property name="src" value="."/> 
   <target name = "generate-javadoc">
      <javadoc packagenames="org.*" sourcepath="${src}" destdir = "doc" failonerror="false" classpath="D:\elk\elasticsearch-2.1.1\lib\*" excludepackagenames="org.elasticsearch.index.q*" useexternalfile="yes" version="true" use="true" windowtitle = "Elasticsearch javadoc">
         <doctitle><![CDATA[= Elasticsearch javadoc =]]></doctitle>
         <bottom>
            <![CDATA[Copyright © iceleng 2016. All Rights Reserved.]]>
         </bottom>
         <group title = "apache packages" packages = "org.apache.*"/>
         <group title = "elasticsearch packages" packages = "org.elasticsearch.*"/>
         <group title = "joda packages" packages = "org.joda.*"/>
      </javadoc>
      <echo message = "java doc has been generated!" />
   </target>
</project>
*/
def response = client.prepareGet("syslog-01.15", "cir.sys.log","AVJEVCNKvyUSxcqpVqZX").get();
println response.getClass().name
println response.headers
println response.isExists()
println response.getSourceAsString()
println response.id
println response.isSourceEmpty()

//https://endymecy.gitbooks.io/elasticsearch-guide-chinese/content/java-api/bulk-api.html
//批量更新
import static org.elasticsearch.common.xcontent.XContentFactory.*;
import org.elasticsearch.action.bulk.*;
BulkRequestBuilder bulkRequest = client.prepareBulk();
// either use client#prepare, or use Requests# to directly build index/delete requests
bulkRequest.add(client.prepareIndex("syslog-01.15", "cir.sys.log")
        .setSource(jsonBuilder()
                    .startObject()
                        .field("user", "kimchy")
                        .field("postDate", new Date())
                        .field("message", "trying out Elasticsearch")
                    .endObject()
                  )
        );

bulkRequest.add(client.prepareIndex("syslog-01.15", "cir.sys.log")
        .setSource(jsonBuilder()
                    .startObject()
                        .field("user", "iceleng")
                        .field("postDate", new Date())
                        .field("message", "another post")
                    .endObject()
                  )
        );

BulkResponse bulkResponse = bulkRequest.execute().actionGet();
if (bulkResponse.hasFailures()) {
    // process failures by iterating through each bulk response item
    println "!!!! Error"
}else{
	println "Bulk insert successful!"
}


client.close();
