package com.elasticsearch.demo.test;


import org.apache.http.HttpHost;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.node.Node;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Test {

    static final Logger logger = LogManager.getLogger(Logger.class.getName());
    /*static Client client;

    @BeforeClass
    public static void setup(){
        Node node = nodeBuilder()
                .clusterName("elasticsearch").client(true).node();
        client = node.client();
    }

    @org.junit.Test
    public void givenJsonString_whenJavaObject_thenIndexDocument() {
        String jsonObject = "{\"age\":20,\"birthDate\":08-19-1997,"
                + "\"name\":\"Olena\"}";
        IndexResponse response = client.prepareIndex("people", "Olena")
                .setSource(jsonObject).get();

        String id = response.getId();
        String index = response.getIndex();
        String type = response.getType();
        long version = response.getVersion();

        assertTrue(response.isCreated());
        assertEquals(0, version);
        assertEquals("people", index);
        assertEquals("Olena", type);
    }*/

    /*public static void main(String[] args) {
        IndexRequest request = new IndexRequest(
                "posts",
                "doc",
                "2");
        String jsonString = "{" +
                "\"user\":\"olena\"," +
                "\"postDate\":\"2018-05-22\"," +
                "\"content\":\"index request using\"" +
                "}";
        request.source(jsonString, XContentType.JSON);
    }*/

    public static void main(String[] args) {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http")));
    }
}
