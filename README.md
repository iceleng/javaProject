# workspace

**2017.02.13**  
[Gradle系列之从init.gradle说起](http://blog.csdn.net/sbsujjbcy/article/details/52079413)  

*2017.02.13*

2016.12.22
Dropwizard-metrics：
https://github.com/dropwizard/metrics
http://metrics.dropwizard.io/3.1.0/manual/core/#reporters
http://metrics.dropwizard.io/3.1.0/apidocs/
http://blog.csdn.net/scutshuxue/article/details/8351810
http://ningg.top/yammer-metrics/

图形化展示指标数据：
https://my.oschina.net/chkui/blog/707632

Metrics 是个什么鬼 之入门教程
http://wuchong.me/blog/2015/08/01/getting-started-with-metrics/
以 Java 为例，目前最为流行的 metrics 库是来自 Coda Hale 的 dropwizard/metrics，该库被广泛地应用于各个知名的开源项目中。例如 Hadoop，Kafka，Spark，JStorm 中。

Metrics —— JVM上的实时监控类库
http://www.jianshu.com/p/e4f70ddbc287
系统开发到一定的阶段，线上的机器越来越多，就需要一些监控了，除了服务器的监控，业务方面也需要一些监控服务。Metrics作为一款监控指标的度量类库，提供了许多工具帮助开发者来完成自定义的监控工作。



2016.12.14
网件(NETGEAR)路由器无线中继设置教程
http://www.192ly.com/router-settings/netgear/wds-2.html
NETGEAR无线路由器无线中继（WDS）功能介绍
http://club.netgear.cn/Knowledgebase/Document.aspx?Did=234
http://net.zol.com.cn/310/3104944.html

2016.5.9
ELK logstash 处理MySQL慢查询日志(26th)(http://www.ttlsa.com/elk/elk-elasticsearch-plugin-management/)
里面有关于解析错误重定向到文件的用法，不错，值得借鉴。



2016.3.4
数据库排名 
http://db-engines.com/en/ranking
DB-Engines排行榜排出了目前最聚人气的数据库管理系统，该排行榜分析了市场上200多个不同类型的数据库。其排行规则是根据
1. 它们在Google和Bing上搜索出的结果数目、
2. Google Trends上的搜索次数、
2. Indeed上的职位数目、
3. LinkedIn中提到的次数、
4. Stackoverflow上的提问以及回复的数量，
这五大因素作为依据所排出的最新榜单。（详情请看 Ranking Method）(http://db-engines.com/en/ranking_definition)

2016.3.3

昨天安装插件失败有可能是logstash的版本问题，0.3.3需要2.1.1版本？

Gemfile:gem "logstash-input-mongodb", "0.3.3", :path => "vendor/local_gems/8c3d662f/logstash-input-mongodb-0.3.3"

Gemfile.jruby-1.9.lock
PATH
  remote: vendor/local_gems/8c3d662f/logstash-input-mongodb-0.3.3
  specs:
    logstash-input-mongodb (0.3.3)
      jdbc-sqlite3 (= 3.8.10.1)
      logstash-codec-plain
      logstash-core (>= 2.0.0.beta2, < 3.0.0)
      mongo (>= 2.0.0)
      sequel
      stud
  logstash-input-mongodb (= 0.3.3)!      

[elk docker setup](https://github.com/elastic/examples/tree/master/ELK_docker_setup)

流程图？

st=>start: Start|past:>http://www.google.com[blank]
e=>end: End:>http://www.google.com
op1=>operation: My Operation|past
op2=>operation: Stuff|current
sub1=>subroutine: My Subroutine|invalid
cond=>condition: Yes 
or No?|approved:>http://www.baidu.com
c2=>condition: Good idea|rejected
io=>inputoutput: catch something...|request

st->op1(right)->cond
cond(yes, right)->c2
cond(no)->sub1(left)->op1
c2(yes)->io->e
c2(no)->op2->e

2016.2.28
[grape简介](http://ifeve.com/groovy-grape/)

2016.2.16
blog.jobbole.com/category/programmer/


2015.8.29
利用Mongodb的复制集搭建高可用分片，Replica Sets + Sharding的搭建过程
http://www.cnblogs.com/javawebsoa/archive/2013/08/10/3249441.html

2015.8.14
Ffmpeg截图rmvb格式视频花屏的问题。发现其实截图花屏不是不支持rmvb的问题，是因为快速跳转导致的，将-ss参数从第一个位置移到后边，再截图就非常清晰了，不过，时间啊，延长了几百倍……
发现mplayer截图速度确实好快，4.3秒搞定rmvb，ffmpeg要6.5秒，慢了近50%了，不过mplayer貌似对超大文件，不如3G以上的mkv和mp4支持不好，经常会hang住，而ffmpeg则不会，所以后续考虑分开处理，对于rmvb都使用mplayer来处理
mplayer截图不能指定文件名！此外，截图的时间戳貌似也不是很准确。
关于图片宽度，目前用-1:240，针对16:9的视频，宽度是426.6，但是对于4:3的视频，只有320，拼接后明显宽度不够，导致convert上去的水印重叠了视频信息。所以可以修改为427:-1，这样就固定了宽度，高度可以按比例去调整。

convert –background none –fill  blue  -font  Sylfaen  -pointsize 48    -stroke green    -strokewidth  2  -gravity center  -rotate 10  label:”Apple” wenzi.jpg

composite  -dissolve 60  -gravity south  -geometry +0+60  wenzi.jpg new.jpg  new_watermark.jpg

   实现文字水印,需要两步,第一步生成文字,使用convert完成, -background控制背景颜色, -fill 控制字体颜色,-font 控制字体, -pointsize 控制字体大小, -stroke控制字体边框颜色,-strokewidth控制边框宽度,-gravity控制位置,-rotate控制旋转的角度,label:"Apple",表示需要生成的文字,wenzi.jpg为要生成的图片.

   使用composite命令行工具添加图片水印, -dissolve控制透明度, -gravity和 -geometry +0+60控制水印文字在背景图片上的位置."+0+60"是相对于前面的 -gravity确定的属性的.(


Groovy 1.6的新特性
http://tech.it168.com/a2009/0514/276/000000276424_all.shtml

@Newify

抓取网络json数据并存入mongodb（1）
http://blog.csdn.net/g1apassz/article/details/43817783
