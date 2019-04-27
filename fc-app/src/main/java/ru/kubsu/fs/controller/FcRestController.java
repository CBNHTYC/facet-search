package ru.kubsu.fs.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kubsu.fs.entity.Detail;
import ru.kubsu.fs.entity.ElastModel;
import ru.kubsu.fs.repository.ElastDao;
import ru.kubsu.fs.repository.FcDao;
import ru.kubsu.fs.schema.DetailsDto.DetailsDtoType;
import ru.kubsu.fs.schema.DetailsDto.ObjectFactory;
import ru.kubsu.fs.schema.QueryParameters.TransferQueryParametersType;
import ru.kubsu.fs.schema.ResponseParameters.TransferQueryResultType;
import ru.kubsu.fs.service.ElastUpdate;
import ru.kubsu.fs.utils.TransferQueryResultTypeTransformer;

import javax.xml.bind.*;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

@RestController
@RequestMapping(path = "/fc/rest", produces = MediaType.APPLICATION_XML_VALUE)
public class FcRestController {

    private final FcDao fcDao;
    private final TransferQueryResultTypeTransformer queryResultTypeMapper;
    private final DetailsMapper detailsMapper;
    private final ElastUpdate update;
    private final ElastDao elastDao;


    @Autowired
    public FcRestController(FcDao fcDao, TransferQueryResultTypeTransformer queryResultTypeMapper, DetailsMapper detailsMapper, ElastUpdate update, ElastDao elastDao) {
        this.fcDao = fcDao;
        this.queryResultTypeMapper = queryResultTypeMapper;
        this.detailsMapper = detailsMapper;
        this.update = update;
        this.elastDao = elastDao;
    }

    @GetMapping(path = "getPhones", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> getProject(@RequestBody String queryXml) throws JAXBException, IOException {
        Unmarshaller unmarshaller = JAXBContext.newInstance(TransferQueryParametersType.class.getPackage().getName()).createUnmarshaller();
        JAXBElement element = (JAXBElement) unmarshaller.unmarshal(new StringReader(queryXml));
        TransferQueryParametersType request = (TransferQueryParametersType) element.getValue();
        List<ElastModel> modelList = elastDao.getPhonesByParameters(request);

        StringWriter sw = new StringWriter();
        TransferQueryResultType resultType = queryResultTypeMapper.transform(modelList);
        Marshaller marshaller = JAXBContext.newInstance(TransferQueryResultType.class.getPackage().getName()).createMarshaller();
        marshaller.marshal(resultType, sw);
        return new ResponseEntity<>(sw.toString(), HttpStatus.OK);

    }

    @GetMapping(path = "getDetails", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> getDetails(@RequestParam("category") String category) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(DetailsDtoType.class.getPackage().getName(),
                DetailsDtoType.class.getClassLoader());
        ObjectFactory objectFactory = new ObjectFactory();

        DetailsDtoType detailsDtoType = objectFactory.createDetailsDtoType();

        List<Detail> detailList = fcDao.getAllDetails();

        detailsDtoType.getDetailDto().addAll(detailsMapper.map(detailList));

        StringWriter sw = new StringWriter();
        Marshaller marshaller = context.createMarshaller();
        marshaller.marshal(detailsDtoType, sw);
        return new ResponseEntity<>(sw.toString(), HttpStatus.OK);
    }

    @PostMapping(path = "uploadPhones")
    public ResponseEntity<HttpStatus> uploadPhones() {
        update.writeAllPhones();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
