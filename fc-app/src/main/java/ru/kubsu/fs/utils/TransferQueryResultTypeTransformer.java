package ru.kubsu.fs.utils;

import lombok.extern.slf4j.Slf4j;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.kubsu.fs.entity.DetailValue;
import ru.kubsu.fs.entity.ElastModel;
import ru.kubsu.fs.entity.Model;
import ru.kubsu.fs.repository.FcDao;
import ru.kubsu.fs.schema.ResponseParameters.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TransferQueryResultTypeTransformer {

    @Autowired
    private FcDao fcDao;

    public TransferQueryResultType transform(List<ElastModel> phoneList) {
        ObjectFactory objectFactory = new ObjectFactory();
        DozerBeanMapper dozerBeanMapper = new DozerBeanMapper();
        TransferQueryResultType resultType = objectFactory.createTransferQueryResultType(); //todo переделать форыч в стрим
        resultType.setHeader(objectFactory.createHeaderType());
        PhoneListType phoneListType = objectFactory.createPhoneListType();
        phoneListType.getPhones().addAll(phoneList.stream().map(phone -> {
            PhoneType phoneType = objectFactory.createPhoneType();
            phoneType.setModel(dozerBeanMapper.map(phone, ModelType.class));
            phoneType.setDetails(dozerBeanMapper.map(phone, DetailType.class));
            phoneType.getImages().addAll(fcDao.getImagesByModelId(Long.valueOf(phone.getModelId())).stream().map(image -> dozerBeanMapper.map(image, ImageType.class)).collect(Collectors.toList()));
            return phoneType;
        }).collect(Collectors.toList()));
        resultType.setPhoneList(phoneListType);
        return resultType;
    }

    private String getArgumentFromDetailValue(String detailName, Model model) {
        Optional<DetailValue> optDetailValue = model.getDetailValueList().stream().filter(detValue -> detValue.getDetail().getName().equals(detailName)).findFirst();
        if (optDetailValue.isPresent()) {
            DetailValue detailValue = optDetailValue.get();
            return detailValue.getValue();
        } else {
            log.error("Не найден аттрибут: '" + detailName + "' для модели с id: " + model.getModelId());
            return "";
        }
    }
}
