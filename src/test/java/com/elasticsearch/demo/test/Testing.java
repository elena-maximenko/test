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

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class Testing {

    static final Logger logger = LogManager.getLogger(Logger.class.getName());

    private final String indexName = "people";
    private final String indexId = "1";
    private final String indexType = "names";

    private static RestHighLevelClient client;

    @BeforeClass
    public static void setupClient() {
        client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http")));
    }

    @Test
    public void createPeopleIndex() throws IOException {

        // request for the index creation
        CreateIndexRequest createIndexRequest = new CreateIndexRequest(indexName);

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

    @Test
    public void putPersonInPeopleIndex() throws IOException {

        IndexRequest request = new IndexRequest(
                indexName,
                indexType,
                indexId
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

        assertEquals(indexId, id);
        assertEquals(indexType, type);
        assertEquals(index, indexName);
    }


    @Test
    public void searchInPeopleIndex()throws IOException{

        String searchKey = "firstName";
        String searchValue = "Olena";

        SearchRequest searchRequest = new SearchRequest(indexName);

        // make a restriction for the search
        searchRequest.types(indexType);

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder(searchKey, searchValue);

        sourceBuilder.query(matchQueryBuilder);

        SearchResponse searchResponse = client.search(searchRequest);

        SearchHits hits = searchResponse.getHits();

        SearchHit[] searchHits = hits.getHits();

        for (SearchHit hit : searchHits) {

            String index = hit.getIndex();
            String type = hit.getType();
            String id = hit.getId();

            System.out.println("index = " + index);
            System.out.println("type = " + type);
            System.out.println("id = " + id);

            assertEquals(indexName, index);
            assertEquals(indexType, type);
            assertEquals(indexId, id);

        }

        // check for people index deleting
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(indexName);

        DeleteIndexResponse deleteIndexResponse = client.indices().delete(deleteIndexRequest);

        boolean deleteAcknowledged = deleteIndexResponse.isAcknowledged();

        assertTrue(deleteAcknowledged);
    }

}