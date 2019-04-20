package ru.kubsu.fs.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kubsu.fs.entity.Detail;
import ru.kubsu.fs.entity.Model;
import ru.kubsu.fs.repository.FcDao;
import ru.kubsu.fs.schema.DetailsDto.DetailsDtoType;
import ru.kubsu.fs.schema.DetailsDto.ObjectFactory;
import ru.kubsu.fs.schema.QueryParameters.TransferQueryParametersType;
import ru.kubsu.fs.schema.ResponseParameters.TransferQueryResultType;
import ru.kubsu.fs.services.DetailsMapper;
import ru.kubsu.fs.services.TransferQueryResultTypeMapper;

import javax.xml.bind.*;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping(path = "/fc/rest", produces = MediaType.APPLICATION_XML_VALUE)
public class FcRestController {

    private final
    FcDao fcDao;

    private final TransferQueryResultTypeMapper queryResultTypeMapper;

    private final DetailsMapper detailsMapper;

    @Autowired
    public FcRestController(FcDao fcDao, TransferQueryResultTypeMapper queryResultTypeMapper, DetailsMapper detailsMapper) {
        this.fcDao = fcDao;
        this.queryResultTypeMapper = queryResultTypeMapper;
        this.detailsMapper = detailsMapper;
    }

    @GetMapping(path = "getPhones", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> getProject(@RequestBody String queryXml) throws JAXBException, SQLException {
        JAXBContext context = JAXBContext.newInstance(TransferQueryParametersType.class.getPackage().getName());
        Unmarshaller unmarshaller = context.createUnmarshaller();
        JAXBElement element = (JAXBElement)unmarshaller.unmarshal(new StringReader(queryXml));
        TransferQueryParametersType request = (TransferQueryParametersType) element.getValue();
        List<Model> modelList= fcDao.getModelList(request);

        StringWriter sw = new StringWriter();
        TransferQueryResultType resultType = queryResultTypeMapper.map(modelList);
        context = JAXBContext.newInstance(TransferQueryResultType.class.getPackage().getName());
        Marshaller marshaller = context.createMarshaller();
        marshaller.marshal(resultType, sw);
        return new ResponseEntity<>(sw.toString(), HttpStatus.OK);
    }

    @GetMapping(path = "getDetails", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> getDetails(@RequestParam("category") String category) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(DetailsDtoType.class.getPackage().getName(),
                DetailsDtoType.class.getClassLoader());
        ObjectFactory objectFactory = new ObjectFactory();

        DetailsDtoType detailsDtoType = objectFactory.createDetailsDtoType();

        List<Detail> detailList = StreamSupport
                .stream(fcDao.getAllDetails().spliterator(), false)
                .collect(Collectors.toList());

        detailsDtoType.getDetailDto().addAll(detailsMapper.map(detailList));

        StringWriter sw = new StringWriter();
        Marshaller marshaller = context.createMarshaller();
        marshaller.marshal(detailsDtoType, sw);
        return new ResponseEntity<>(sw.toString(), HttpStatus.OK);
    }


}
