package ru.kubsu.fs.utils;

import org.dozer.DozerBeanMapper;
import org.springframework.stereotype.Component;
import ru.kubsu.fs.dto.response.*;
import ru.kubsu.fs.entity.ElastModel;

@Component
public class PhoneTypeMapper {
    public PhoneType mapPhoneType(ElastModel elastModel) {
        DozerBeanMapper dozerBeanMapper = new DozerBeanMapper();
        PhoneType phoneType = new PhoneType();
        ImageListType imageListType = new ImageListType();
        phoneType.setModel(dozerBeanMapper.map(elastModel, ModelType.class));
        phoneType.setDetails(dozerBeanMapper.map(elastModel, DetailType.class));
        imageListType.setImageLocationList(elastModel.getImageLocationList());
        phoneType.setImages(imageListType);
        return phoneType;
    }

    public AccessoriesType mapAccessoriesType(ElastModel elastModel) {
        DozerBeanMapper dozerBeanMapper = new DozerBeanMapper();
        AccessoriesType accessoriesType = new AccessoriesType();
        ImageListType accImageListType = new ImageListType();
        accessoriesType.setModel(dozerBeanMapper.map(elastModel, ModelType.class));
        accessoriesType.setDetails(dozerBeanMapper.map(elastModel, DetailType.class));
        accImageListType.setImageLocationList(elastModel.getImageLocationList());
        accessoriesType.setImages(accImageListType);
        return accessoriesType;
    }
}
