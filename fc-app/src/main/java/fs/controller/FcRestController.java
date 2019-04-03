package fs.controller;

import fs.entity.Phone;
import fs.repository.FcDao;
import fs.services.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kubsu.fs.schema.QueryParameters.TransferQueryParametersType;
import ru.kubsu.fs.schema.ResponseParameters.TransferQueryResultType;

import javax.xml.bind.*;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping(path = "/fc/rest", produces = MediaType.APPLICATION_XML_VALUE)
public class FcRestController {

    private final
    FcDao fcDao;

    private final Mapper maper;

    @Autowired
    public FcRestController(FcDao fcDao, Mapper maper) {
        this.fcDao = fcDao;
        this.maper = maper;
    }

    @GetMapping(path = "getPhones", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> getProject(@RequestBody String queryXml) throws JAXBException, SQLException {
        JAXBContext context = JAXBContext.newInstance(TransferQueryParametersType.class.getPackage().getName());
        Unmarshaller unmarshaller = context.createUnmarshaller();
        JAXBElement element = (JAXBElement)unmarshaller.unmarshal(new StringReader(queryXml));
        TransferQueryParametersType request = (TransferQueryParametersType) element.getValue();
        List<Phone> phones= fcDao.getPhonesList(request);

        StringWriter sw = new StringWriter();
        TransferQueryResultType resultType = maper.map(phones);
        context = JAXBContext.newInstance(TransferQueryResultType.class.getPackage().getName());
        Marshaller marshaller = context.createMarshaller();
        marshaller.marshal(resultType, sw);
        return new ResponseEntity<>(sw.toString(), HttpStatus.OK);
    }
}
