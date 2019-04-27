package ru.kubsu.fs.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import ru.kubsu.fs.repository.ElastDao;
import ru.kubsu.fs.repository.FcDao;
import ru.kubsu.fs.service.ElastUpdate;
import ru.kubsu.fs.utils.DetailsMapper;
import ru.kubsu.fs.utils.TransferQueryResultTypeTransformer;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

@RestController
@RequestMapping(path = "/fc/rest", produces = MediaType.APPLICATION_JSON_VALUE)
public class FcRestController {

    private final FcDao fcDao;
    private final TransferQueryResultTypeTransformer queryResultTypeMapper;
    private final DetailsMapper detailsMapper;
    private final ElastUpdate update;
    private final ElastDao elastDao;

    private final String PHONES = "phones";


    @Autowired
    public FcRestController(FcDao fcDao, TransferQueryResultTypeTransformer queryResultTypeMapper, DetailsMapper detailsMapper, ElastUpdate update, ElastDao elastDao) {
        this.fcDao = fcDao;
        this.queryResultTypeMapper = queryResultTypeMapper;
        this.detailsMapper = detailsMapper;
        this.update = update;
        this.elastDao = elastDao;
    }

    @GetMapping(path = "getPhones", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getProject(@RequestBody String queryJson) throws JAXBException, IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        StringWriter sw = new StringWriter();
        ParametrizedQuery query = objectMapper.readValue(queryJson, ParametrizedQuery.class);
        List<ElastModel> modelList = elastDao.getPhonesByParameters(query);
        PhonesResponse phonesResponse = queryResultTypeMapper.transform(modelList);
        objectMapper.writeValue(sw, phonesResponse);
        return new ResponseEntity<>(sw.toString(), HttpStatus.OK);
    }

    @GetMapping(path = "getDetails", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getDetails(@RequestParam("category") String category) throws IOException {
        switch (category) {
            case PHONES:
                ObjectMapper objectMapper = new ObjectMapper();
                DetailsDto detailsDto = new DetailsDto();
                List<Detail> detailList = fcDao.getAllDetails();
                detailsDto.setDetailDtoList(detailsMapper.map(detailList));
                StringWriter sw = new StringWriter();
                objectMapper.writeValue(sw, detailsDto);
                return new ResponseEntity<>(sw.toString(), HttpStatus.OK);
            default:
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(path = "uploadPhones")
    public ResponseEntity<HttpStatus> uploadPhones() {
        update.writeAllPhones();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
