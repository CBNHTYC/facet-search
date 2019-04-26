package ru.kubsu.fs.repository;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.kubsu.fs.entity.ElastModel;
import ru.kubsu.fs.schema.QueryParameters.ObjectFactory;
import ru.kubsu.fs.schema.QueryParameters.RangeParameterType;
import ru.kubsu.fs.schema.QueryParameters.TransferQueryParametersType;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ElastDaoTest {


    @Autowired
    private ElastDao elastDao;

    @Test
    public void getPhonesByParameters() throws IOException {
        ObjectFactory objectFactory = new ObjectFactory();
        TransferQueryParametersType transferQueryParametersType = objectFactory.createTransferQueryParametersType();
        TransferQueryParametersType.QueryParameters queryParameters = objectFactory.createTransferQueryParametersTypeQueryParameters();

        RangeParameterType rangeParameterType = objectFactory.createRangeParameterType();
        rangeParameterType.setName("price");
        rangeParameterType.setValueBegin("100");
        rangeParameterType.setValueEnd("1000");
        queryParameters.getRangeParameter().add(rangeParameterType);
        transferQueryParametersType.setQueryParameters(queryParameters);
        List<ElastModel> elastModelList = elastDao.getPhonesByParameters(transferQueryParametersType);

        Assert.assertTrue(elastModelList.size() > 0);
    }
}