package ru.kubsu.fs.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kubsu.fs.entity.Model;
import ru.kubsu.fs.repository.ElastDao;
import ru.kubsu.fs.repository.FcDao;
import ru.kubsu.fs.utils.ElastModelMapper;

import java.util.List;

@Service
public class ElastUpdate {

    private final FcDao fcDao;
    private final ElastDao elastDao;
    private final ElastModelMapper elastModelMapper;

    @Autowired
    public ElastUpdate(FcDao fcDao, ElastDao elastDao, ElastModelMapper elastModelMapper) {
        this.fcDao = fcDao;
        this.elastDao = elastDao;
        this.elastModelMapper = elastModelMapper;
    }

    public void writeAllPhones() {
        List<Model> modelList = fcDao.getAllModels();
        elastDao.putPhoneRecordList(elastModelMapper.map(modelList));
    }
}
