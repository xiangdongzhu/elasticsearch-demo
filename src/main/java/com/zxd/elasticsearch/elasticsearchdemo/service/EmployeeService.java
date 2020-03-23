package com.zxd.elasticsearch.elasticsearchdemo.service;

import com.zxd.elasticsearch.elasticsearchdemo.domain.Employee;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

@Service
public class EmployeeService {
    @Autowired
    private RestHighLevelClient client;


    /**
     * 添加员工信息
     *
     * @param employee
     * @return
     */
    public String addEmployee(Employee employee) {
        try {
            XContentBuilder content = XContentFactory
                    .jsonBuilder()
                    .startObject()
                    .field("name", employee.getName())
                    .field("age", employee.getAge())
                    .field("position", employee.getPosition())
                    .field("country", employee.getCountry())
                    .field("join_date", employee.getJoin_date())
                    .field("salary", employee.getSalary())
                    .endObject();
            IndexRequest request = new IndexRequest("employee").source(content);
            IndexResponse response = client.index(request, RequestOptions.DEFAULT);
            return response.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping("/empSearch")
    public String boolQuery(Employee employee) {
        //构建bool查询
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        //判断查询字段是否为空
        if (!StringUtils.isEmpty(employee.getPosition())) {
            boolQuery.must(
                    QueryBuilders.matchQuery("position", employee.getPosition()));
        }
        if (employee.getAge() != null) {
            RangeQueryBuilder rangeQuery = QueryBuilders
                    .rangeQuery("age")
                    .from(employee.getGtage())
                    .to(employee.getLtage());
            boolQuery.filter(rangeQuery);
        }
        //默认配置
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        // 设置搜索
        sourceBuilder.query(boolQuery);
        //设置完成后，就可以添加到 SearchRequest 中。
        SearchRequest searchRequest = new SearchRequest("employee");
        searchRequest.source(sourceBuilder);
        String result = "";
        try {
            SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
            String lineSeparator = System.getProperty("line.separator", "\n");
            SearchHits hits = response.getHits();
            for (SearchHit hit : hits) {
                result += hit.getSourceAsString() + lineSeparator;
            }
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping("/searchAggre")
    public String searchAggre() throws Exception {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        TermsAggregationBuilder aggregation = AggregationBuilders
                .terms("group_by_country")
                .field("country")
                .subAggregation(AggregationBuilders
                        .dateHistogram("group_by_join_date")
                        .field("join_date")
                        .dateHistogramInterval(DateHistogramInterval.YEAR)
                        .subAggregation(AggregationBuilders.avg("avg_salary")
                                .field("salary")));
        SearchRequest searchRequest = new SearchRequest("employee_1");
        searchRequest.source(searchSourceBuilder.aggregation(aggregation));

        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);

        Map<String, Aggregation> aggMap = response.getAggregations().asMap();
        StringTerms groupByCountry = (StringTerms) aggMap.get("group_by_country");
        Iterator<StringTerms.Bucket> groupByCountryBucketIterator = groupByCountry.getBuckets().iterator();
        while (groupByCountryBucketIterator.hasNext()) {
            Bucket groupByCountryBucket = groupByCountryBucketIterator.next();
            System.out.println(groupByCountryBucket.getKey() + ":" + groupByCountryBucket.getDocCount());
            Histogram groupByJoinDate = (Histogram) groupByCountryBucket.getAggregations().asMap().get("group_by_join_date");
            Iterator groupByJoinDateBucketIterator =  groupByJoinDate.getBuckets().iterator();

            while (groupByJoinDateBucketIterator.hasNext()) {
                org.elasticsearch.search.aggregations.bucket.histogram.Histogram.Bucket groupByJoinDateBucket = (Histogram.Bucket) groupByJoinDateBucketIterator.next();
                System.out.println(groupByJoinDateBucket.getKey() + ":" + groupByJoinDateBucket.getDocCount());
                Avg avg = (Avg) groupByJoinDateBucket.getAggregations().asMap().get("avg_salary");
                System.out.println(avg.getValue());
            }
        }
        return null;
    }

}
