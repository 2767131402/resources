package com.bbd.elasticsearch;

import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.index.reindex.DeleteByQueryRequestBuilder;
import org.elasticsearch.rest.RestStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class DemoApplicationTests2 {

	@Autowired
	private TransportClient client;

	@Test
	public void deleteByQuery() {
		BulkByScrollResponse response =
				new DeleteByQueryRequestBuilder(client, DeleteByQueryAction.INSTANCE)
						.filter(QueryBuilders.termQuery("id", "2"))
						.source("item")
						.get();
		long deleted = response.getDeleted();
		System.out.println(deleted);
	}

	/**
	 * 插入一条文档
	 */
	@Test
	public void add(){
		String json = "{\n" +
				"          \"id\" : 2,\n" +
				"          \"title\" : \"坚果手机R1\",\n" +
				"          \"category\" : \" 手机锤子\",\n" +
				"          \"price\" : 8888,\n" +
				"          \"images\" : \"http://image.baidu.com/13123.jpg\"\n" +
				"        }";
		IndexResponse response = client.prepareIndex("item", "_doc")
				.setSource(json, XContentType.JSON)
				.get();

		String id = response.getId();
		System.out.println(id);
	}

	/**
	 * 插入多条记录
	 */
	@Test
	public void addMany(){
		String json1 = "{\n" +
				"          \"id\" : 4,\n" +
				"          \"title\" : \"坚果手机R2\",\n" +
				"          \"category\" : \" 手机锤子\",\n" +
				"          \"price\" : 9999,\n" +
				"          \"images\" : \"http://image.baidu.com/13123.jpg\"\n" +
				"        }";
		String json2 = "{\n" +
				"          \"id\" : 5,\n" +
				"          \"title\" : \"坚果手机R3\",\n" +
				"          \"category\" : \" 手机锤子\",\n" +
				"          \"price\" : 6666,\n" +
				"          \"images\" : \"http://image.baidu.com/13123.jpg\"\n" +
				"        }";
		String json3 = "{\n" +
				"          \"id\" : 6,\n" +
				"          \"title\" : \"华为P30\",\n" +
				"          \"category\" : \" 手机华为\",\n" +
				"          \"price\" : 5555,\n" +
				"          \"images\" : \"http://image.baidu.com/13123.jpg\"\n" +
				"        }";

		String[] strs = {json1,json2,json3};
		BulkRequestBuilder bulkRequest = client.prepareBulk();
		for (String json: strs) {
			IndexRequestBuilder builder = client.prepareIndex("item", "_doc");
			builder.setSource(json,XContentType.JSON);
			bulkRequest.add(builder);
		}
		BulkResponse response = bulkRequest.get();
		System.out.println(response);
	}

	/**
	 * 更新一条记录
	 * 字段不存在-插入
	 */
	@Test
	public void update() throws Exception{
		UpdateRequest updateRequest = new UpdateRequest("item","_doc","olURC3EBBTTDdAwBzCVN");
		updateRequest.doc(XContentFactory.jsonBuilder()
				.startObject()
				.field("category", "锤子手机")
				.endObject());
		UpdateResponse response = client.update(updateRequest).get();

		System.out.println(response);
	}

	/**
	 * 一次更新多条记录
	 * 字段不存在-插入
	 */
	@Test
	public void updateMany() throws Exception{
		String[] strs = {"olURC3EBBTTDdAwBzCVN","7FUQC3EBBTTDdAwBRST_","bVUPC3EBBTTDdAwBRSQ-"};
		BulkRequestBuilder bulkRequest = client.prepareBulk();
		for (String id:strs) {
			UpdateRequestBuilder builder = client.prepareUpdate("item", "_doc", id);
			builder.setDoc(XContentFactory.jsonBuilder().startObject().field("name","手机").endObject());
			bulkRequest.add(builder);
		}
		BulkResponse response = bulkRequest.get();
		System.out.println(response);
	}

	/**
	 * 删除
	 */
	@Test
	public void delete(){
		DeleteResponse response = client.prepareDelete("item", "_doc", "1").setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE).get();
		RestStatus status = response.status();
		System.out.println(status.getStatus());
	}
}
