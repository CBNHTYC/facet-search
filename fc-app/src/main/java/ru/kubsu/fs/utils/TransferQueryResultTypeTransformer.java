package ru.kubsu.fs.utils;

import lombok.extern.slf4j.Slf4j;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.kubsu.fs.dto.response.*;
import ru.kubsu.fs.entity.ElastModel;
import ru.kubsu.fs.entity.Image;
import ru.kubsu.fs.repository.FcDao;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TransferQueryResultTypeTransformer {

    @Autowired
    private FcDao fcDao;

    public PhonesResponse transform(List<ElastModel> phoneList) {
        DozerBeanMapper dozerBeanMapper = new DozerBeanMapper();
        PhonesResponse phonesResponse = new PhonesResponse();
        phonesResponse.setPhoneTypeList(phoneList.stream().map(phone -> {
            PhoneType phoneType = new PhoneType();
            ImageListType imageListType = new ImageListType();
            phoneType.setModel(dozerBeanMapper.map(phone, ModelType.class));
            phoneType.setDetails(dozerBeanMapper.map(phone, DetailType.class));
            imageListType.setImageLocations(fcDao.getImagesByModelId(Long.valueOf(phone.getModelId())).stream().map(Image::getLocation).collect(Collectors.toList()));
            return phoneType;
        }).collect(Collectors.toList()));
        return phonesResponse;
    }

    public PhonesResponse transform(ElastModel elastModel) {
        DozerBeanMapper dozerBeanMapper = new DozerBeanMapper();
        PhonesResponse phonesResponse = new PhonesResponse();
        PhoneType phoneType = new PhoneType();
        ImageListType imageListType = new ImageListType();
        phoneType.setModel(dozerBeanMapper.map(elastModel, ModelType.class));
        phoneType.setDetails(dozerBeanMapper.map(elastModel, DetailType.class));
        imageListType.setImageLocations(fcDao.getImagesByModelId(Long.valueOf(elastModel.getModelId())).stream().map(Image::getLocation).collect(Collectors.toList()));
        phonesResponse.setPhoneTypeList(Collections.singletonList(phoneType));
        return phonesResponse;
    }
}