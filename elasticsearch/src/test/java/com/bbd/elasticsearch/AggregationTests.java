package com.bbd.elasticsearch;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.ExtendedBounds;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.sum.Sum;
import org.elasticsearch.search.aggregations.metrics.sum.SumAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.valuecount.ValueCount;
import org.elasticsearch.search.aggregations.metrics.valuecount.ValueCountAggregationBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Iterator;
import java.util.Map;

/**
 * 聚合查询
 */
@SpringBootTest
class AggregationTests {

	@Autowired
	private TransportClient client;

	@Test
	public void findAll() {
		SearchRequestBuilder requestBuilder = client.prepareSearch("item");
		SearchResponse searchResponse = requestBuilder.execute().actionGet();
		SearchHits hits = searchResponse.getHits();
		Iterator<SearchHit> iterator = hits.iterator();
		while (iterator.hasNext()) {
			SearchHit searchHit = iterator.next();
			String s = searchHit.getSourceAsString();
			System.out.println(s);
		}
	}
//  数据：
//	{"id":2,"title":"坚果手机R1","category":"锤子","price":3699.0,"images":"http://image.baidu.com/13123.jpg"}
//	{"id":3,"title":"华为META10","category":"华为","price":4499.0,"images":"http://image.baidu.com/13123.jpg"}
//	{"id":1,"title":"苹果XSMax","category":"小米","price":3499.0,"images":"http://image.baidu.com/13123.jpg"}


	/**
	 * 相当于：select category,count(*) as player_count from item group by category
	 */
	@Test
	public void testCount() {
		SearchRequestBuilder requestBuilder = client.prepareSearch("item");
		TermsAggregationBuilder field = AggregationBuilders.terms("player_count").field("category.keyword");
		requestBuilder.addAggregation(field);
		SearchResponse searchResponse = requestBuilder.execute().actionGet();
		Terms hits = searchResponse.getAggregations().get("player_count");
		hits.getBuckets().forEach(bucket->{
			System.out.println("category:"+bucket.getKeyAsString()+" count:"+bucket.getDocCount());
		});
	}

	/**
	 * 相当于：select category,sum(price) as player_count from item group by category
	 * 同样适用于 max/min/sum/avg
	 */
	@Test
	public void testSum() {
		SearchRequestBuilder requestBuilder = client.prepareSearch("item");
		TermsAggregationBuilder one = AggregationBuilders.terms("category_count").field("category.keyword");
		SumAggregationBuilder two = AggregationBuilders.sum("sumprice").field("price");

		requestBuilder.addAggregation(one.subAggregation(two));
		SearchResponse searchResponse = requestBuilder.execute().actionGet();
		Terms agg = searchResponse.getAggregations().get("category_count");
		
		agg.getBuckets().forEach(a->{
			Sum sumprice = a.getAggregations().get("sumprice");
			System.out.println("category:"+a.getKeyAsString()+" sum:"+sumprice.getValue());
		});
	}

	/**
	 * 相当于：select category,title,count(*) as category_count from item group by category,title
	 */
	@Test
	public void testMoreFile() {
		SearchRequestBuilder requestBuilder = client.prepareSearch("item");
		TermsAggregationBuilder one1 = AggregationBuilders.terms("category").field("category.keyword");
		TermsAggregationBuilder one2 = AggregationBuilders.terms("title").field("title.keyword");

		requestBuilder.addAggregation(one1.subAggregation(one2));
		SearchResponse searchResponse = requestBuilder.execute().actionGet();
		Terms agg = searchResponse.getAggregations().get("category");

		agg.getBuckets().forEach(a->{
			Terms title = a.getAggregations().get("title");
			title.getBuckets().forEach(i->{
				System.out.println("category:"+a.getKeyAsString()+" title:"+i.getKeyAsString()+" count:"+a.getDocCount());
			});
		});
	}

	/**
	 * 相当于：select category,count(*) as category_count,sum(price) as price_sum from item group by category
	 */
	@Test
	public void testMoreFile1() {
		SearchRequestBuilder requestBuilder = client.prepareSearch("item");
		TermsAggregationBuilder one1 = AggregationBuilders.terms("category").field("category.keyword");

		ValueCountAggregationBuilder two1 = AggregationBuilders.count("category_count").field("category.keyword");
		SumAggregationBuilder two2 = AggregationBuilders.sum("price_sum").field("price");

		requestBuilder.addAggregation(one1.subAggregation(two1).subAggregation(two2));
		SearchResponse searchResponse = requestBuilder.execute().actionGet();
		Terms agg = searchResponse.getAggregations().get("category");

		agg.getBuckets().forEach(a->{
			ValueCount count = a.getAggregations().get("category_count");
			Sum sum = a.getAggregations().get("price_sum");
			System.out.println("category:"+a.getKeyAsString()+" count:"+count.getValue()+" sum:"+sum.getValue());
		});
	}
	/**
	 * ******
	 * 相当于：select category,count(*) as category_count,sum(price) as price_sum from item group by category
	 * ******
	 */
	@Test
	public void testMoreFile1_() {
		SearchRequestBuilder requestBuilder = client.prepareSearch("item");
		TermsAggregationBuilder one1 = AggregationBuilders.terms("category").field("category.keyword");
		SumAggregationBuilder two2 = AggregationBuilders.sum("price_sum").field("price");

		requestBuilder.addAggregation(one1.subAggregation(two2));
		SearchResponse searchResponse = requestBuilder.execute().actionGet();
		Terms agg = searchResponse.getAggregations().get("category");

		agg.getBuckets().forEach(a->{
			Sum sum = a.getAggregations().get("price_sum");
			System.out.println("category:"+a.getKeyAsString()+" count:"+a.getDocCount()+" sum:"+sum.getValue());
		});
	}

	/**
	 * 相当于：select category,title,sum(price) as price_sum from item group by category,title
	 */
	@Test
	public void testMoreFile2() {
		SearchRequestBuilder requestBuilder = client.prepareSearch("item");
		TermsAggregationBuilder one1 = AggregationBuilders.terms("category").field("category.keyword");
		TermsAggregationBuilder one2 = AggregationBuilders.terms("title").field("title.keyword");

		SumAggregationBuilder two2 = AggregationBuilders.sum("price_sum").field("price");
		requestBuilder.addAggregation(one1.subAggregation(one2).subAggregation(two2));
		SearchResponse searchResponse = requestBuilder.execute().actionGet();
		Terms agg = searchResponse.getAggregations().get("category");

		agg.getBuckets().forEach(a->{
			Sum sum = a.getAggregations().get("price_sum");
			Terms te = a.getAggregations().get("title");
			te.getBuckets().forEach(b->{
				System.out.println("category:"+a.getKeyAsString()+" title:"+b.getKeyAsString()+" price:"+sum.getValue());
			});
		});
	}


    /**
     * 类似于
     * select category,count(*) from item
     * union all
     * select title,count(*) from item
     */
	@Test
	public void test() {
		SearchRequestBuilder requestBuilder = client.prepareSearch("item");
		TermsAggregationBuilder one1 = AggregationBuilders.terms("category").field("category.keyword");
		TermsAggregationBuilder one2 = AggregationBuilders.terms("title").field("title.keyword");

		requestBuilder.addAggregation(one1);
		requestBuilder.addAggregation(one2);
		SearchResponse searchResponse = requestBuilder.execute().actionGet();

		Terms agg1 = searchResponse.getAggregations().get("category");
		agg1.getBuckets().forEach(a->{
			System.out.println("category:"+a.getKeyAsString()+" count:"+a.getDocCount());
		});
		System.out.println("---------");
		Terms agg2 = searchResponse.getAggregations().get("title");
		agg2.getBuckets().forEach(a->{
			System.out.println("title:"+a.getKeyAsString()+" count:"+a.getDocCount());
		});
	}

	/**
	 * 时间聚合
	 */
//	#1 参数强制返回空桶。
//	#2 参数强制返回全年数据。
//	两个附加的参数会强制响应返回全年所有月份的信息，无论它们文档数目如何。min_doc_count 很容易理解：即使桶是空的也会强制作为结果返回。
//	需要对 extended_bounds 参数做些许解释。 min_doc_count 参数强制空桶信息返回，但是 Elasticsearch 默认只会返回处于最小与最大值之间的数据。
//	所以如果数据在四月和七月之间，我们的桶只会表示这之间的月份（无论是不是空）。为了得到全年的信息，我们需要告诉 Elasticsearch 我们想要得到那些处于最小值和最大值之外的桶的信息。

//	GET /cars/transactions/_search
//	{
//		"size" : 0,
//		"aggs": {
//			"sales": {
//				"date_histogram": {
//					"field": "sold",
//					"interval": "month",
//					"format": "yyyy-MM-dd",
//					"min_doc_count" : 0, //可以返回没有数据的月份
//					"extended_bounds" : { //强制返回数据的范围
//						"min" : "2014-01-01",
//						"max" : "2014-12-31"
//					}
//				}
//			}
//		}
//	}
	public void dateAgg(){
		SearchResponse response = client.prepareSearch("cars")
				.setTypes("transactions")
				.addAggregation(
						AggregationBuilders.dateHistogram("sales")
								.field("sold")
								.dateHistogramInterval(DateHistogramInterval.MONTH)
								.format("yyyy-MM-dd")
								.minDocCount(0l)
								.extendedBounds(
										new ExtendedBounds("2014-01-01","2014-12-31")
								)
				)
				.setSize(0)
				.get();
	}
}
