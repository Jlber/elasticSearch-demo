package com.example.elbert.elasticSearchdemo;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;

import static org.elasticsearch.common.xcontent.XContentFactory.*;
/**
 * Created by elbert on 2017/12/31.
 */
@RestController
@RequestMapping("es")
public class EsController {
    @Autowired
    private TransportClient client;
    @RequestMapping("/getclient")
    public String getClient(){
       client.listedNodes().forEach(
               node-> System.out.println(node.getName()));
       client.connectedNodes().forEach(
               node-> System.out.println(node.toString())
       );
       return "ok";
    }

    @RequestMapping("/createIndex")
    public String createIndex() throws IOException {
        for (int i=1;i<10;i++) {
            XContentBuilder builder = jsonBuilder()
                    .startObject()
                    .field("user", "jeq"+i)
                    .field("postDate", LocalDateTime.now())
                    .field("message", i+" Elasticsearch")
                    .field("sex", "boy")
                    .endObject();
            String json = builder.string();
            IndexResponse response = client.prepareIndex("twitter", "tweet",String.valueOf(i))
                    .setSource(json, XContentType.JSON)
                    .get();
            System.out.println(response.toString());
        }
        return "ok";
    }
    @RequestMapping("/getResponse")
    public String getResponse(){
        GetResponse response = client.prepareGet("bank", "account", "1")
                .get();

        return  response.toString();
    }
    @RequestMapping("/delete")
    public String delete(){
        BulkByScrollResponse response =
                DeleteByQueryAction.INSTANCE.newRequestBuilder(client)
                        .filter(QueryBuilders.matchQuery("user", "kimchy"))
                        .source("twitter")
                        .get();

        long deleted = response.getDeleted();
        return  response.toString();
    }
    @RequestMapping("/update")
    public String update() throws IOException {
        XContentBuilder xb=   jsonBuilder()
                .startObject()
                .field("sex", "girl")
                .endObject();
        String json=xb.string();
        System.out.println(json);
       UpdateResponse response= client.prepareUpdate("twitter", "tweet", "AWCriKOx-a3pdaeA_Vcg")
                .setDoc(xb)
                .get();
        return response.toString();
    }

    /**
     * 1 mustnot 直接加入到filter里了？
     * 2 term 放filter和 must里的区别
     * @return
     */
    @RequestMapping("/query1")
    public String boolQueryTest(){
        BoolQueryBuilder boolQueryBuilder=QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.matchQuery("gender","M"))
                .must(QueryBuilders.matchQuery("state","AR"))
               .mustNot(QueryBuilders.matchQuery("lastname","Suarez"))
                .filter(QueryBuilders.rangeQuery("age").gte(25).lte(50));

        System.out.println(boolQueryBuilder.toString());
        System.out.println("fasdass"
        );
        SearchRequestBuilder searchRequestBuilder = client
                .prepareSearch("bank")
                .setQuery(boolQueryBuilder);
     SearchResponse response =searchRequestBuilder.get();

        return  response.toString();

    }
}
