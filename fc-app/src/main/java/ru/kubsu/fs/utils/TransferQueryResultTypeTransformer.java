package ru.kubsu.fs.utils;

import lombok.extern.slf4j.Slf4j;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.kubsu.fs.dto.response.*;
import ru.kubsu.fs.entity.ElastModel;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TransferQueryResultTypeTransformer {

    @Autowired
    PhoneTypeMapper phoneTypeMapper;

    public PhonesResponse transform(List<ElastModel> phoneList) {
        PhonesResponse phonesResponse = new PhonesResponse();
        phonesResponse.setPhoneTypeList(phoneList.stream().map(phoneTypeMapper::mapPhoneType).collect(Collectors.toList()));
        return phonesResponse;
    }

    public PhonesResponse transform(ElastModel phone, List<ElastModel> accessories) {
        PhonesResponse phonesResponse = new PhonesResponse();
        PhoneType phoneType = phoneTypeMapper.mapPhoneType(phone);
        phoneType.setAccessories(accessories.stream().map(phoneTypeMapper::mapAccessoriesType).collect(Collectors.toList()));
        phonesResponse.setPhoneTypeList(Collections.singletonList(phoneType));
        return phonesResponse;
    }

    public PhonesResponse transformAddit(List<ElastModel> phoneList) {
        PhonesResponse phonesResponse = new PhonesResponse();
        phonesResponse.setAdditionalList(phoneList.stream().map(phoneTypeMapper::mapPhoneType).collect(Collectors.toList()));
        return phonesResponse;
    }
}