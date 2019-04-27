package ru.kubsu.fs.repository;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.kubsu.fs.dto.query.ParametrizedQuery;
import ru.kubsu.fs.dto.query.SimpleParameter;
import ru.kubsu.fs.entity.ElastModel;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ElastDaoTest {


    @Autowired
    private ElastDao elastDao;

    @Test
    public void getPhonesByParameters() throws IOException {
        ParametrizedQuery parametrizedQuery = new ParametrizedQuery();

        SimpleParameter simpleParameter = new SimpleParameter();
        simpleParameter.setName("vendor");
        simpleParameter.setValue("acer");
        parametrizedQuery.setSimpleParameter(Arrays.asList(simpleParameter));
        List<ElastModel> elastModelList = elastDao.getPhonesByParameters(parametrizedQuery);
        Assert.assertTrue(elastModelList.size() > 0);
    }
}