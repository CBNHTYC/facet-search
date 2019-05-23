package ru.kubsu.fs.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import javassist.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kubsu.fs.dto.details.DetailsDto;
import ru.kubsu.fs.dto.query.ParametrizedQuery;
import ru.kubsu.fs.dto.response.PhonesResponse;
import ru.kubsu.fs.entity.Detail;
import ru.kubsu.fs.entity.ElastModel;
import ru.kubsu.fs.model.DetailDictionary;
import ru.kubsu.fs.model.DetailsEnum;
import ru.kubsu.fs.model.PhoneInfo;
import ru.kubsu.fs.repository.ElastDao;
import ru.kubsu.fs.repository.FcDao;
import ru.kubsu.fs.utils.DetailsMapper;
import ru.kubsu.fs.utils.TransferQueryResultTypeTransformer;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/fc/rest", produces = MediaType.APPLICATION_JSON_VALUE)
public class FcRestController {

    private final FcDao fcDao;
    private final TransferQueryResultTypeTransformer queryResultTypeMapper;
    private final DetailsMapper detailsMapper;
    private final ElastDao elastDao;

    private final String PHONES = "phones";


    @Autowired
    public FcRestController(FcDao fcDao, TransferQueryResultTypeTransformer queryResultTypeMapper, DetailsMapper detailsMapper, ElastDao elastDao) {
        this.fcDao = fcDao;
        this.queryResultTypeMapper = queryResultTypeMapper;
        this.detailsMapper = detailsMapper;
        this.elastDao = elastDao;
    }

    @PostMapping(path = "getPhones", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getProject(@RequestBody String queryJson) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        StringWriter sw = new StringWriter();
        ParametrizedQuery query = objectMapper.readValue(queryJson, ParametrizedQuery.class);
        List<ElastModel> modelList = elastDao.getPhonesByParameters(query);
        if (modelList.size() > 0) {
            PhonesResponse phonesResponse = queryResultTypeMapper.transform(modelList);
            objectMapper.writeValue(sw, phonesResponse);
        } else {
            modelList = elastDao.getAdditPhonesByParameters(query);
            PhonesResponse phonesResponse = queryResultTypeMapper.transformAddit(modelList);
            objectMapper.writeValue(sw, phonesResponse);
        }
        return new ResponseEntity<>(sw.toString(), HttpStatus.OK);
    }

    @GetMapping(path = "getMostViewedPhoneList", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getPhoneById() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        StringWriter sw = new StringWriter();
        List<ElastModel> modelList = elastDao.getMostViewedPhones();
        PhonesResponse phonesResponse = queryResultTypeMapper.transform(modelList);
        objectMapper.writeValue(sw, phonesResponse);
        return new ResponseEntity<>(sw.toString(), HttpStatus.OK);
    }

    @GetMapping(path = "phone", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getPhoneById(@RequestParam("id") String id) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            StringWriter sw = new StringWriter();
            PhoneInfo phoneInfo = elastDao.getPhoneById(id);
            PhonesResponse phonesResponse = queryResultTypeMapper.transform(phoneInfo.getPhone(), phoneInfo.getAccessories());
            objectMapper.writeValue(sw, phonesResponse);
            return new ResponseEntity<>(sw.toString(), HttpStatus.OK);
        } catch (NotFoundException nfe) {
            return new ResponseEntity<>("Телефон не найден", HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>("Ошибка сервера", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(path = "accessory", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getAccessoryById(@RequestParam("id") String id) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            StringWriter sw = new StringWriter();
            ElastModel elastModel = elastDao.getAccessoryById(id);
            PhonesResponse phonesResponse = queryResultTypeMapper.transform(elastModel, Collections.emptyList());
            objectMapper.writeValue(sw, phonesResponse);
            return new ResponseEntity<>(sw.toString(), HttpStatus.OK);
        } catch (NotFoundException nfe) {
            return new ResponseEntity<>("Аксессуар не найден", HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>("Ошибка сервера", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(path = "getDetails", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getDetails(@RequestParam("category") String category) throws IOException {
        switch (category) {
            case PHONES:
                ObjectMapper objectMapper = new ObjectMapper();
                DetailsDto detailsDto = new DetailsDto();
                List<Detail> detailList = fcDao.getAllDetails();
                detailsDto.setDetailDtoList(detailsMapper.map(detailList));
                detailsDto.getDetailDtoList().forEach(detailDto -> {
                    DetailDictionary detailDictionary = Arrays.stream(DetailsEnum.values()).filter(detailsEnum -> detailsEnum.getValue().getRu().equals(detailDto.getName())).findAny().get().getValue();
                    try {
                        detailDto.setElastDetailValueList(elastDao.getDetaiValuesByDetailName(detailDictionary.getEn()));
                    } catch (IOException e) {
                        log.error(e.getMessage());
                    }
                });
                StringWriter sw = new StringWriter();
                objectMapper.writeValue(sw, detailsDto);
                return new ResponseEntity<>(sw.toString(), HttpStatus.OK);
            default:
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(path = "search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> fulTextSearch(@RequestParam("query") String query) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        StringWriter sw = new StringWriter();
        List<ElastModel> modelList = elastDao.fullTextSearch(query);
        PhonesResponse phonesResponse = queryResultTypeMapper.transform(modelList);
        objectMapper.writeValue(sw, phonesResponse);
        return new ResponseEntity<>(sw.toString(), HttpStatus.OK);
    }

//    @GetMapping(path = "getPopAccess", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<String> getPopAccess(@RequestParam("modelId") String modelId) throws IOException {
//        ObjectMapper objectMapper = new ObjectMapper();
//        StringWriter sw = new StringWriter();
//        List<ElastModel> modelList = elastDao.fullTextSearch(query);
//        PhonesResponse phonesResponse = queryResultTypeMapper.transform(modelList);
//        objectMapper.writeValue(sw, phonesResponse);
//        return new ResponseEntity<>(sw.toString(), HttpStatus.OK);
//    }
}
