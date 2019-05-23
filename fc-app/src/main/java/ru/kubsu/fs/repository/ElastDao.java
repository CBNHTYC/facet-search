package ru.kubsu.fs.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import javassist.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.dozer.DozerBeanMapper;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.kubsu.fs.dto.query.ParametrizedQuery;
import ru.kubsu.fs.dto.query.RangeParameter;
import ru.kubsu.fs.dto.query.SimpleParameter;
import ru.kubsu.fs.entity.ElastDetailValue;
import ru.kubsu.fs.entity.ElastModel;
import ru.kubsu.fs.model.PhoneInfo;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ElastDao {
    @Value("${elasticsearch.request.size}")
    Integer requestSize;

    @Value("${elasticsearch.request.plate.size}")
    Integer plateRequestSize;

    @Value("${elasticsearch.request.access.recommend.size}")
    Integer accessSize;

    public static final String PHONES_INDEX_ALIAS = "phones";
    public static final String MODEL_ID_FIELD = "modelId";
    public static final String VIEW_FIELD = "views";

    public static final String VENDOR_FIELD= "vendor";
    public static final String MODEL_FIELD= "modelName";
    public static final String DESCRIPTION_FIELD= "description";
    public static final String POWER_TYPE_FIELD= "powerType";
    public static final String DIAGONAL_FIELD= "diagonal";
    public static final String CATEGORY_FIELD= "category";

    public static final String POWER_CAT= "3";
    public static final String CASE_CAT= "5";

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private ObjectMapper objectMapper;

    private ActionListener<UpdateResponse> updateListener = new ActionListener<UpdateResponse>() {
        @Override
        public void onResponse(UpdateResponse updateResponse) {
            log.info("Elastic document updated: " + updateResponse.getGetResult().getIndex() + updateResponse.getGetResult().getType() + updateResponse.getGetResult().getId() + "with status: " + updateResponse.status().getStatus());
        }

        @Override
        public void onFailure(Exception e) {
            log.error("Elastic document update failed: " + e.getMessage());
        }
    };

    public void putPhoneRecordList(List<ElastModel> elastModelList) {
        Optional.ofNullable(elastModelList).orElse(Collections.emptyList())
                .forEach(this::putPhoneRecord);
    }

    public void putDetailValueList(List<ElastDetailValue> elastDetailValueList, String detailName) {
        Optional.ofNullable(elastDetailValueList).orElse(Collections.emptyList())
                .forEach(elastDetailValue -> putDetailValue(elastDetailValue, detailName));
    }

    public void putPhoneRecord(ElastModel elastModel) {
        try {
            String phoneJson = objectMapper.writeValueAsString(elastModel);
            IndexRequest request = new IndexRequest(PHONES_INDEX_ALIAS, "phone");
            request.source(phoneJson, XContentType.JSON);
            IndexResponse indexResponse = restHighLevelClient.index(request, RequestOptions.DEFAULT);
            log.debug(indexResponse.toString());
        } catch (Exception ex) {
            log.warn("", ex);
        }
    }

    public void putDetailValue(ElastDetailValue elastDetailValue, String detailName) {
        try {
            String detailJson = objectMapper.writeValueAsString(elastDetailValue);
            IndexRequest request = new IndexRequest(detailName, "detailValue");
            request.source(detailJson, XContentType.JSON);
            IndexResponse indexResponse = restHighLevelClient.index(request, RequestOptions.DEFAULT);
            log.debug(indexResponse.toString());
        } catch (Exception ex) {
            log.error("", ex);
        }
    }

    public PhoneInfo getPhoneById(String id) throws IOException, NotFoundException {
        SearchRequest searchRequest = new SearchRequest(PHONES_INDEX_ALIAS);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        QueryBuilder query = QueryBuilders.boolQuery()
                .must(QueryBuilders.matchQuery(MODEL_ID_FIELD, id));

        searchSourceBuilder.query(query);
        searchSourceBuilder.size(requestSize);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        Optional<SearchHit> optionalSearchHit = Arrays.stream(searchResponse.getHits().getHits()).findFirst();
        if (optionalSearchHit.isPresent()) {
            PhoneInfo phoneInfo = new PhoneInfo();
            ElastModel elastModel = new DozerBeanMapper().map(optionalSearchHit.get().getSourceAsMap(), ElastModel.class);
            elastModel.setViews(elastModel.getViews() + 1);

            UpdateRequest updateRequest = new UpdateRequest(PHONES_INDEX_ALIAS, "phone", optionalSearchHit.get().getId());
            String phoneJson = objectMapper.writeValueAsString(elastModel);
            updateRequest.doc(phoneJson, XContentType.JSON);
            restHighLevelClient.updateAsync(updateRequest, RequestOptions.DEFAULT, updateListener);

            List<ElastModel> accessories = getPopAccess(DIAGONAL_FIELD, elastModel.getDiagonal(), CASE_CAT);
            accessories.addAll(getPopAccess(POWER_TYPE_FIELD, elastModel.getPowerType(), POWER_CAT));

            if (accessories.size() > 9) {
                accessories = accessories.subList(0, 8);
            }

            phoneInfo.setPhone(elastModel);
            phoneInfo.setAccessories(accessories);
            return phoneInfo;
        } else {
            throw new NotFoundException("Телефон с указанным id не найден");
        }
    }

    public ElastModel getAccessoryById(String id) throws IOException, NotFoundException {
        SearchRequest searchRequest = new SearchRequest(PHONES_INDEX_ALIAS);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        QueryBuilder query = QueryBuilders.boolQuery()
                .must(QueryBuilders.matchQuery(MODEL_ID_FIELD, id));

        searchSourceBuilder.query(query);
        searchSourceBuilder.size(requestSize);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        Optional<SearchHit> optionalSearchHit = Arrays.stream(searchResponse.getHits().getHits()).findFirst();
        if (optionalSearchHit.isPresent()) {
            ElastModel elastModel = new DozerBeanMapper().map(optionalSearchHit.get().getSourceAsMap(), ElastModel.class);
            elastModel.setViews(elastModel.getViews() + 1);
            return elastModel;
        } else {
            throw new NotFoundException("Телефон с указанным id не найден");
        }
    }

    public List<ElastModel> getPhonesByParameters(ParametrizedQuery parametrizedQuery) throws IOException {
        SearchRequest searchRequest = new SearchRequest(PHONES_INDEX_ALIAS);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        QueryBuilder query = QueryBuilders.boolQuery()
                .must(QueryBuilders.matchAllQuery());

        Set<String> nameSet = parametrizedQuery.getSimpleParameter().stream().map(SimpleParameter::getName).collect(Collectors.toSet());

        Optional<List<SimpleParameter>> optionalSimpleTypes = Optional.ofNullable(parametrizedQuery.getSimpleParameter());
        if (optionalSimpleTypes.isPresent()) {
            List<SimpleParameter> simpleParameterList = optionalSimpleTypes.get();
            nameSet.forEach(s -> {
                List<String []> paramValList = simpleParameterList.stream()
                        .filter(simpleParameter -> s.equals(simpleParameter.getName()))
                        .map(simpleParameter -> {
                            String paramVal = simpleParameter.getValue();
                            paramVal = paramVal.toLowerCase();
                            return paramVal.split("\\s");
                        }).collect(Collectors.toList());
                List<String> wordsList = new ArrayList<>();
                for (String[] words : paramValList) {
                    wordsList.addAll(Arrays.asList(words));
                }
                ((BoolQueryBuilder) query).filter(QueryBuilders.termsQuery(s, wordsList));
            });
        }

        Optional<List<RangeParameter>> optionalRangeTypes = Optional.ofNullable(parametrizedQuery.getRangeParameter());
        if (optionalRangeTypes.isPresent()) {
            List<RangeParameter> rangeParameterList = optionalRangeTypes.get();
            rangeParameterList.forEach(rangeParameter -> {
                RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery(rangeParameter.getName());
                rangeQuery.from(rangeParameter.getValueBegin());
                rangeQuery.to(rangeParameter.getValueEnd());
                ((BoolQueryBuilder) query).filter(rangeQuery);
            });
        }

        searchSourceBuilder.query(query);
        searchSourceBuilder.size(requestSize);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        return Arrays.stream(searchResponse.getHits().getHits()).map(hit -> new DozerBeanMapper().map(hit.getSourceAsMap(), ElastModel.class)).collect(Collectors.toList());
    }

    public List<ElastModel> getAdditPhonesByParameters(ParametrizedQuery parametrizedQuery) {
        List<ElastModel> elastModelList = new ArrayList<>();
        ParametrizedQuery parametrizedQueryTmp = new ParametrizedQuery();
        parametrizedQueryTmp.setRangeParameter(parametrizedQuery.getRangeParameter());
        if (Optional.ofNullable(parametrizedQuery.getSimpleParameter()).isPresent()) {
            parametrizedQuery.getSimpleParameter().forEach(simpleParameter -> {
                List<SimpleParameter> simpleParameterTmpList = new ArrayList<>(parametrizedQuery.getSimpleParameter());
                simpleParameterTmpList.remove(simpleParameter);
                parametrizedQueryTmp.setSimpleParameter(simpleParameterTmpList);
                try {
                    elastModelList.addAll(getPhonesByParameters(parametrizedQueryTmp));
                } catch (IOException e) {
                    log.error("Ошибка при поиске аналогичных моделей по параметрам: " + e.getMessage());
                }
            });
        }
        if (Optional.ofNullable(parametrizedQuery.getRangeParameter()).isPresent()) {
            parametrizedQueryTmp.setSimpleParameter(parametrizedQuery.getSimpleParameter());
            parametrizedQuery.getRangeParameter().forEach(rangeParameter -> {
                List<RangeParameter> rangeParameterTmpList = new ArrayList<>(parametrizedQuery.getRangeParameter());
                rangeParameterTmpList.remove(rangeParameter);
                parametrizedQueryTmp.setRangeParameter(rangeParameterTmpList);
                try {
                    elastModelList.addAll(getPhonesByParameters(parametrizedQueryTmp));
                } catch (IOException e) {
                    log.error("Ошибка при поиске аналогичных моделей по параметрам: " + e.getMessage());
                }
            });
        }
        elastModelList.sort(Comparator.comparingInt(ElastModel::getViews));
        return (elastModelList.subList(0, 9));
    }

    public List<ElastModel> getMostViewedPhones() throws IOException {
        SearchRequest searchRequest = new SearchRequest(PHONES_INDEX_ALIAS);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        QueryBuilder query = QueryBuilders.boolQuery()
                .must(QueryBuilders.matchAllQuery());

        ((BoolQueryBuilder) query).filter(QueryBuilders.matchQuery(CATEGORY_FIELD, "2"));
        searchSourceBuilder.sort(new FieldSortBuilder(VIEW_FIELD).order(SortOrder.DESC));
        searchSourceBuilder.query(query);
        searchSourceBuilder.size(plateRequestSize);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        return Arrays.stream(searchResponse.getHits().getHits()).map(hit -> new DozerBeanMapper().map(hit.getSourceAsMap(), ElastModel.class)).collect(Collectors.toList());
    }

    public List<ElastDetailValue> getDetaiValuesByDetailName(String name) throws IOException {
        SearchRequest searchRequest = new SearchRequest(name);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        QueryBuilder query = QueryBuilders.boolQuery()
                .must(QueryBuilders.matchAllQuery());

        searchSourceBuilder.query(query);
        searchSourceBuilder.size(requestSize);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        return Arrays.stream(searchResponse.getHits().getHits()).map(hit -> new DozerBeanMapper().map(hit.getSourceAsMap(), ElastDetailValue.class)).collect(Collectors.toList());
    }

    public List<ElastModel> fullTextSearch(String textQuery) throws IOException {
        SearchRequest searchRequest = new SearchRequest(PHONES_INDEX_ALIAS);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        QueryBuilder query = QueryBuilders.boolQuery()
                .must(QueryBuilders.multiMatchQuery(textQuery, MODEL_FIELD, VENDOR_FIELD, DESCRIPTION_FIELD));

        searchSourceBuilder.query(query);
        searchSourceBuilder.size(requestSize);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        return Arrays.stream(searchResponse.getHits().getHits()).map(hit -> new DozerBeanMapper().map(hit.getSourceAsMap(), ElastModel.class)).collect(Collectors.toList());
    }

    public List<ElastModel> getPopAccess(String detField, String detVal, String catVal) throws IOException {
        SearchRequest searchRequest = new SearchRequest(PHONES_INDEX_ALIAS);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        QueryBuilder query = QueryBuilders.boolQuery()
                .must(QueryBuilders.matchAllQuery());

        ((BoolQueryBuilder) query).filter(QueryBuilders.matchQuery(detField, detVal));
        ((BoolQueryBuilder) query).filter(QueryBuilders.matchQuery(CATEGORY_FIELD, catVal));

        searchSourceBuilder.query(query);
        searchSourceBuilder.size(accessSize);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        return Arrays.stream(searchResponse.getHits().getHits()).map(hit -> new DozerBeanMapper().map(hit.getSourceAsMap(), ElastModel.class)).collect(Collectors.toList());
    }
}
