package com.elasticsearch.demo.test;


import org.apache.http.HttpHost;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.node.Node;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class Test {

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

    @org.junit.Test
    public void createPeopleIndex() throws IOException {
        CreateIndexRequest createIndexRequest = new CreateIndexRequest(indexName);

        createIndexRequest.settings(Settings.builder()
                .put("index.number_of_shards", 4)
                .put("index.number_of_replicas", 3)
        );

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

    @org.junit.Test
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

    @org.junit.Test
    public void deletePeopleIndex() throws IOException {
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(indexName);

        DeleteIndexResponse deleteIndexResponse = client.indices().delete(deleteIndexRequest);

        boolean deleteAcknowledged = deleteIndexResponse.isAcknowledged();

        assertTrue(deleteAcknowledged);
    }



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


   /*/ RIGHT STUFF IS GOING ON HERE
    public static void main(String[] args) throws IOException {


        String indexName = "people";

        //connection with elastic search
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http")));

        CreateIndexRequest createIndexRequest = new CreateIndexRequest(indexName);

        createIndexRequest.settings(Settings.builder()
                .put("index.number_of_shards", 4)
                .put("index.number_of_replicas", 3)
        );

        createIndexRequest.mapping("names",
                "  {\n" +
                        "    \"names\": {\n" +
                        "      \"properties\": {\n" +

                        "        \"firstName\": {\n" +
                        "          \"type\": \"text\"\n" +
                        "        },\n" +

                        "\"lastName\": {\n" +
                        "          \"type\": \"text\"\n" +
                        "        }\n" +

                        "  }" +
                        "  }" +
                        "  }",
                XContentType.JSON);


        CreateIndexResponse createIndexResponse = client.indices().create(createIndexRequest);

        boolean acknowledged = createIndexResponse.isAcknowledged();
        boolean shardsAcknowledged = createIndexResponse.isShardsAcknowledged();

        System.out.println("acknowledged = " + acknowledged);
        System.out.println("shardsAcknowledged = " + shardsAcknowledged);


        IndexRequest request = new IndexRequest(
                indexName,
                "names",
                "1"
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
        long version = indexResponse.getVersion();

        System.out.println("index = " + index);
        System.out.println("type = " + type);
        System.out.println("id = " + id);
        System.out.println("version = " + version);

        client.close();
    }*/
}
