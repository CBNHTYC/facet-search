package ru.kubsu.fs.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.dozer.DozerBeanMapper;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.kubsu.fs.entity.ElastModel;
import ru.kubsu.fs.schema.QueryParameters.RangeParameterType;
import ru.kubsu.fs.schema.QueryParameters.SimpleParameterType;
import ru.kubsu.fs.schema.QueryParameters.TransferQueryParametersType;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ElastDao {
    @Value("${elasticsearch.request.size}")
    Integer requestSize;

    public static final String EVENT_LOG_INDEX_ALIAS = "phones";

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private ObjectMapper objectMapper;

    public void putPhoneRecordList(List<ElastModel> elastModelList) {
        Optional.ofNullable(elastModelList).orElse(Collections.emptyList())
                .forEach(this::putPhoneRecord);
    }

    public void putPhoneRecord(ElastModel elastModel) {
        try {
            try {
                String eventJson = objectMapper.writeValueAsString(elastModel);
                IndexRequest request = new IndexRequest(EVENT_LOG_INDEX_ALIAS, "phone");
                request.source(eventJson, XContentType.JSON);
                IndexResponse indexResponse = restHighLevelClient.index(request, RequestOptions.DEFAULT);
                log.debug(indexResponse.toString());
            } catch (Exception e) {
                log.warn("", e);
            }
        } catch (Exception ex) {
            log.warn("", ex);
        }
    }

    public List<ElastModel> getPhonesByParameters(TransferQueryParametersType queryParametersType) throws IOException {
        SearchRequest searchRequest = new SearchRequest("phones");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        QueryBuilder query = QueryBuilders.boolQuery()
                .must(QueryBuilders.matchAllQuery());

        Optional<List<SimpleParameterType>> optionalSimpleTypes = Optional.ofNullable(queryParametersType.getQueryParameters().getSimpleParameter());
        if (optionalSimpleTypes.isPresent()) {
            List<SimpleParameterType> simpleParameterTypeList = optionalSimpleTypes.get();
            simpleParameterTypeList.forEach(simpleParameterType -> ((BoolQueryBuilder) query).filter(QueryBuilders.matchQuery(simpleParameterType.getName(), simpleParameterType.getValue())));
        }

        Optional<List<RangeParameterType>> optionalRangeTypes = Optional.ofNullable(queryParametersType.getQueryParameters().getRangeParameter());
        if (optionalRangeTypes.isPresent()) {
            List<RangeParameterType> rangeParameterTypeList = optionalRangeTypes.get();
            rangeParameterTypeList.forEach(rangeParameterType -> {
                RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery(rangeParameterType.getName());
                rangeQuery.from(rangeParameterType.getValueBegin());
                rangeQuery.to(rangeParameterType.getValueEnd());
                ((BoolQueryBuilder) query).filter(rangeQuery);
            });
        }

        searchSourceBuilder.query(query);
        searchSourceBuilder.size(requestSize);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        return Arrays.stream(searchResponse.getHits().getHits()).map(hit -> new DozerBeanMapper().map(hit.getSourceAsMap(), ElastModel.class)).collect(Collectors.toList());
    }
}
