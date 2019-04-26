package ru.kubsu.fs.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kubsu.fs.entity.Detail;
import ru.kubsu.fs.entity.Model;
import ru.kubsu.fs.repository.ElastDao;
import ru.kubsu.fs.repository.FcDao;
import ru.kubsu.fs.schema.ResponseParameters.PhoneType;
import ru.kubsu.fs.utils.PhoneTypeMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ElastUpdate {

    @Autowired
    private FcDao fcDao;
    @Autowired
    private PhoneTypeMapper phoneTypeMapper;
    @Autowired
    private ElastDao elastDao;

    public void writeAllPhones () {
        List<PhoneType> phoneTypeList;
        List<Model> modelList = fcDao.getAllModels();
        List<Detail> detailList = fcDao.getAllDetails();
        phoneTypeList = modelList.stream().map(model -> phoneTypeMapper.map(model, detailList)).collect(Collectors.toList());

        elastDao.putPhoneRecordList(phoneTypeList);
    }
}
