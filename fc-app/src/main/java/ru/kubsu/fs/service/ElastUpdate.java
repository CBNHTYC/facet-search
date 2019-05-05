package ru.kubsu.fs.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kubsu.fs.entity.*;
import ru.kubsu.fs.model.DetailDictionary;
import ru.kubsu.fs.model.DetailsEnum;
import ru.kubsu.fs.repository.ElastDao;
import ru.kubsu.fs.repository.FcDao;
import ru.kubsu.fs.utils.ElastModelMapper;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public void writeDetailValues() {
        List<Model> modelList = fcDao.getAllModels();
        HashSet<String> modelNameSet = new HashSet<>();
        modelList.forEach(model -> modelNameSet.add(model.getName()));
        elastDao.putDetailValueList(modelNameSet.stream().map(modelName -> new ElastDetailValue(modelName, "")).collect(Collectors.toList()), "model");

        List<Vendor> vendorList = fcDao.getAllVendors();
        HashSet<String> vendorNameSet = new HashSet<>();
        vendorList.forEach(vendor -> vendorNameSet.add(vendor.getName()));
        elastDao.putDetailValueList(vendorNameSet.stream().map(vendorName -> new ElastDetailValue(vendorName, "")).collect(Collectors.toList()), "vendor");

        List<Detail> detailList = fcDao.getAllDetails().stream().filter(detail -> detail.getDetailValueList().size() > 0).collect(Collectors.toList());
        detailList.forEach(detail -> {
            HashSet<String> detailValueSet = new HashSet<>();
            String unit = detail.getDetailValueList().get(0).getUnit();
            detail.getDetailValueList().forEach(detailValue -> detailValueSet.add(detailValue.getValue()));
            Optional<DetailsEnum> optionalDetailsEnum = Arrays.stream(DetailsEnum.values()).filter(detailsEnum -> detailsEnum.getValue().getRu().equals(detail.getName())).findAny();
            optionalDetailsEnum.ifPresent(detailsEnum -> elastDao.putDetailValueList(detailValueSet.stream().map(detailValue -> new ElastDetailValue(detailValue, unit)).collect(Collectors.toList()), detailsEnum.getValue().getEn()));
        });
    }
}
