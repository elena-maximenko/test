package com.elasticsearch.demo.test;


import org.apache.http.HttpHost;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;

import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;

import org.elasticsearch.index.query.MatchQueryBuilder;

import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class Testing {

    private static final Logger LOGGER = LogManager.getLogger(Logger.class.getName());

    private static final String INDEX_NAME = "people";
    private static final String INDEX_ID = "1";
    private static final String INDEX_TYPE = "names";

    private static RestHighLevelClient client;

    @BeforeClass
    public static void setupClient() {

        // set up the connection with elasticsearch
        client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http")));
    }

    @Test(priority = 1)
    public void createPeopleIndex() throws IOException {

        // request for the index creation
        CreateIndexRequest createIndexRequest = new CreateIndexRequest(INDEX_NAME);

        createIndexRequest.settings(Settings.builder()
                .put("index.number_of_shards", 4)
                .put("index.number_of_replicas", 3)
        );

        // define index
        createIndexRequest.mapping("names",
                "  {\n" +
                        "    \"names\": {\n" +
                        "      \"properties\": {\n" +

                        "        \"firstName\": {\n" +
                        "          \"type\": \"text\"\n" +
                        "        },\n" +

                        "        \"lastName\": {\n" +
                        "          \"type\": \"text\"\n" +
                        "        }\n" +

                        "  }" +
                        "  }" +
                        "  }",
                XContentType.JSON);

        CreateIndexResponse createIndexResponse = client.indices().create(createIndexRequest);

        boolean acknowledged = createIndexResponse.isAcknowledged();
        boolean shardsAcknowledged = createIndexResponse.isShardsAcknowledged();

        assertTrue(acknowledged && shardsAcknowledged);
    }

    @Test(priority = 2)
    public void putPersonInPeopleIndex() throws IOException {

        IndexRequest request = new IndexRequest(
                INDEX_NAME,
                INDEX_TYPE,
                INDEX_ID
        );

        String jsonString = "{" +
                "\"firstName\":\"Olena\"," +
                "\"lastName\":\"Maksymenko\"" +
                "}";

        request.source(jsonString, XContentType.JSON);

        IndexResponse indexResponse = client.index(request);

        String index = indexResponse.getIndex();
        String type = indexResponse.getType();
        String id = indexResponse.getId();

        assertEquals(INDEX_ID, id);
        assertEquals(INDEX_TYPE, type);
        assertEquals(index, INDEX_NAME);
    }


    @Test(priority = 3)
    public void searchInPeopleIndex()throws IOException{

        String searchKey = "firstName";
        String searchValue = "Olena";

        SearchRequest searchRequest = new SearchRequest(INDEX_NAME);

        // make a restriction for the search
        searchRequest.types(INDEX_TYPE);

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        // query for search matching
        MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder(searchKey, searchValue);

        sourceBuilder.query(matchQueryBuilder);

        SearchResponse searchResponse = client.search(searchRequest);

        // matching items
        SearchHits hits = searchResponse.getHits();

        SearchHit[] searchHits = hits.getHits();

        //for each match
        for (SearchHit hit : searchHits) {

            String index = hit.getIndex();
            String type = hit.getType();
            String id = hit.getId();

            assertEquals(INDEX_NAME, index);
            assertEquals(INDEX_TYPE, type);
            assertEquals(INDEX_ID, id);

        }
    }

    @AfterClass
    public void deletePeopleIndex() throws IOException{

        // check for people index deleting
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(INDEX_NAME);

        DeleteIndexResponse deleteIndexResponse = client.indices().delete(deleteIndexRequest);

        boolean deleteAcknowledged = deleteIndexResponse.isAcknowledged();

        assertTrue(deleteAcknowledged);
    }
}

