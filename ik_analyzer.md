===配置近义词
<http://www.cnblogs.com/yjf512/p/4789239.html>

===Elasticsearch 使用IK分词，如何配置同义词？
<http://elasticsearch.cn/?/question/29>

===In ElasticSearch should we use default_field in query String for nGram analyzer
<http://stackoverflow.com/questions/10518404/in-elasticsearch-should-we-use-default-field-in-query-string-for-ngram-analyzer>

===Elasticsearch Analyzer 的内部机制
<http://mednoter.com/all-about-analyzer-part-one.html>

===Mixed-Language Fields
<https://www.elastic.co/guide/en/elasticsearch/guide/master/mixed-lang-fields.html>

===IK Analysis for ElasticSearch
<https://github.com/medcl/elasticsearch-analysis-ik>

===ES中的“store”，“index”，“_all”，“_source”都是什么意思？
<http://queirozf.com/entries/what-do-store-index-all-source-mean-in-elasticsearch>

http://localhost:9200/iktest/_analyze?analyzer=ik&pretty=true&text=我是中国人


===测试分词用的索引设置
    {
      "iktest": {
        "aliases": {},
        "mappings": {
          "_default_": {
            "_all": {
              "enabled": false
            }
          },
          "resource": {
            "dynamic": "false",
            "_all": {
              "enabled": false
            },
            "properties": {
              "title": {
                "type": "string",
                "fields": {
                  "cn": {
                    "type": "string",
                    "analyzer": "ik"
                  },
                  "en": {
                    "type": "string",
                    "analyzer": "english"
                  }
                }
              }
            }
          }
        },
        "settings": {
          "index": {
            "creation_date": "1453866321274",
            "refresh_interval": "5s",
            "number_of_shards": "1",
            "number_of_replicas": "0",
            "uuid": "6AM2xyTARMKZdREl48iQfw",
            "version": {
              "created": "2010199"
            }
          }
        },
        "warmers": {}
      }
    }
