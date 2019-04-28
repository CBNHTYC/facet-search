package ru.kubsu.fs.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.kubsu.fs.dto.query.ParametrizedQuery;
import ru.kubsu.fs.dto.query.RangeParameter;
import ru.kubsu.fs.dto.query.SimpleParameter;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;

@RestController
@RequestMapping(path = "/fc/service", produces = MediaType.APPLICATION_JSON_VALUE)
public class ServiceController {

    @GetMapping(path = "getQueryExample", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getQueryExample () throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        StringWriter sw = new StringWriter();
        ParametrizedQuery parametrizedQuery = new ParametrizedQuery();
        SimpleParameter simpleParameter = new SimpleParameter();
        RangeParameter rangeParameter = new RangeParameter();
        simpleParameter.setName("SimpleName");
        simpleParameter.setValue("SimpleValue");
        rangeParameter.setName("RangeName");
        rangeParameter.setValueBegin("ValueBegin");
        rangeParameter.setValueEnd("ValueEnd");
        parametrizedQuery.setSimpleParameter(Arrays.asList(simpleParameter));
        parametrizedQuery.setRangeParameter(Arrays.asList(rangeParameter));
        mapper.writeValue(sw, parametrizedQuery);
        return new ResponseEntity<>(sw.toString(), HttpStatus.OK);
    }
}