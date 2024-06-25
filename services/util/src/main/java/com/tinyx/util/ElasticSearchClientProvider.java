package com.tinyx.util;

import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import org.apache.http.HttpHost;
import io.quarkus.arc.DefaultBean;

@ApplicationScoped
public class ElasticSearchClientProvider {

    @Produces
    @ApplicationScoped
    @DefaultBean
    public RestHighLevelClient restHighLevelClient() {
        return new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200, "http"))
        );
    }
}