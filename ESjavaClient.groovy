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
def response = client.prepareGet("movie", "tongse","AVHFDXvdaDuWNqsrIHjA").get();
println response.headers
//println response.exists()
//println response.sourceAsString()
println response.source
println response.id
println response.isSourceEmpty()

client.close();
