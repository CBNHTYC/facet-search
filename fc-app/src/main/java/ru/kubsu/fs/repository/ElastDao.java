package ru.kubsu.fs.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.kubsu.fs.schema.ResponseParameters.PhoneType;


import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ElastDao {

    public static final String EVENT_LOG_INDEX_ALIAS = "phones";
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private ObjectMapper objectMapper;

    public void putPhoneRecordList(List<PhoneType> phones){
        Optional.ofNullable(phones).orElse(Collections.emptyList())
                .stream()
                .forEach(this::putPhoneRecord);
    }

    public void putPhoneRecord(PhoneType phone) {
        try {
            String eventJson = objectMapper.writeValueAsString(phone);
            IndexRequest request = new IndexRequest(EVENT_LOG_INDEX_ALIAS,"phone");
            request.source(eventJson, XContentType.JSON);
            IndexResponse indexResponse = restHighLevelClient.index(request, RequestOptions.DEFAULT);
            logger.debug(indexResponse.toString());
        } catch (Exception ex){
            logger.warn("",ex);
        }
    }

}
