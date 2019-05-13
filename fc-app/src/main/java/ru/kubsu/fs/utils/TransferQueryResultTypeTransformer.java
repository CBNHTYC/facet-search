package ru.kubsu.fs.utils;

import lombok.extern.slf4j.Slf4j;
import org.dozer.DozerBeanMapper;
import org.springframework.stereotype.Component;
import ru.kubsu.fs.dto.response.*;
import ru.kubsu.fs.entity.ElastModel;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TransferQueryResultTypeTransformer {

    public PhonesResponse transform(List<ElastModel> phoneList) {
        DozerBeanMapper dozerBeanMapper = new DozerBeanMapper();
        PhonesResponse phonesResponse = new PhonesResponse();
        phonesResponse.setPhoneTypeList(phoneList.stream().map(phone -> {
            PhoneType phoneType = new PhoneType();
            ImageListType imageListType = new ImageListType();
            phoneType.setModel(dozerBeanMapper.map(phone, ModelType.class));
            phoneType.setDetails(dozerBeanMapper.map(phone, DetailType.class));
            imageListType.setImageLocationList(phone.getImageLocationList());
            phoneType.setImages(imageListType);
            return phoneType;
        }).collect(Collectors.toList()));
        return phonesResponse;
    }

    public PhonesResponse transform(ElastModel phone, List<ElastModel> accessories) {
        DozerBeanMapper dozerBeanMapper = new DozerBeanMapper();
        PhonesResponse phonesResponse = new PhonesResponse();
        PhoneType phoneType = new PhoneType();
        ImageListType imageListType = new ImageListType();
        phoneType.setModel(dozerBeanMapper.map(phone, ModelType.class));
        phoneType.setDetails(dozerBeanMapper.map(phone, DetailType.class));
        imageListType.setImageLocationList(phone.getImageLocationList());
        phoneType.setImages(imageListType);

        phoneType.setAccessories(accessories.stream().map(accessory -> {
            AccessoriesType accessoriesType = new AccessoriesType();
            ImageListType accImageListType = new ImageListType();
            accessoriesType.setModel(dozerBeanMapper.map(accessory, ModelType.class));
            accessoriesType.setDetails(dozerBeanMapper.map(accessory, DetailType.class));
            accImageListType.setImageLocationList(accessory.getImageLocationList());
            accessoriesType.setImages(accImageListType);
            return accessoriesType;
        }).collect(Collectors.toList()));

        phonesResponse.setPhoneTypeList(Collections.singletonList(phoneType));
        return phonesResponse;
    }
}