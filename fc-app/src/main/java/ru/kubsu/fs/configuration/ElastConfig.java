package ru.kubsu.fs.configuration;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElastConfig {
    @Value("${events.log.elasticsearch.server}")
    private String elasticServer;

    @Bean
    public RestHighLevelClient elasticsearchRestClient() {
        return new RestHighLevelClient(
                RestClient.builder(
                        HttpHost.create(elasticServer)
                )
        );
    }
}
