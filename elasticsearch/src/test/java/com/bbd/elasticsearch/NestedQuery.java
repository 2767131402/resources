package com.bbd.elasticsearch;

import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 嵌套查询
 */
@SpringBootTest
public class NestedQuery {

    /**
     * 嵌套查询
     * 数据：nested.json
     */
    @Test
    public void test(){
        QueryBuilder orderItemsQuery = QueryBuilders.nestedQuery("orderItem.orderItems",
                QueryBuilders.boolQuery()
                        .must(QueryBuilders.matchQuery("orderItem.orderItems.stockSite", "0001"))
                        .must(QueryBuilders.matchQuery("orderItem.orderItems.skuNo", "154018")),
                ScoreMode.Total);

        QueryBuilder orderQuery = QueryBuilders.nestedQuery("orderItem",
                QueryBuilders.boolQuery()
                        .must(QueryBuilders.matchQuery("orderItem.orderType", "RO"))
                        .must(QueryBuilders.matchQuery("orderItem.orderDate", "20170708")),
                ScoreMode.Total);

        QueryBuilder queryBuilder = QueryBuilders.boolQuery().must(orderQuery).must(orderItemsQuery);

//        Iterable<OrderItem> it = orderRepository.search(queryBuilder);
//        List<OrderItem> list = Lists.newArrayList(it);
//        System.out.println(list);
    }
}
