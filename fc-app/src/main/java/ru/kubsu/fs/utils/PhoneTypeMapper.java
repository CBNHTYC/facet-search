package ru.kubsu.fs.utils;

import lombok.extern.slf4j.Slf4j;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.kubsu.fs.entity.Detail;
import ru.kubsu.fs.entity.DetailValue;
import ru.kubsu.fs.entity.Model;
import ru.kubsu.fs.schema.ResponseParameters.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
public class PhoneTypeMapper {
    private final long ADDITIONAL_DETAIL_ID = 0;

    @Autowired
    private DetailsMapper detailsMapper;
    @Autowired
    private DozerBeanMapper dozerBeanMapper;


    public PhoneType map(Model model, List<Detail> detailList) {
        ObjectFactory objectFactory = new ObjectFactory();

        PhoneType phoneType = objectFactory.createPhoneType();
        List<ImageType> imageTypeList;
        List<DetailType> detailTypeList;

        ModelType modelType = objectFactory.createModelType();
        modelType.setId(String.valueOf(model.getModelId()));
        modelType.setName(model.getName());
        modelType.setVendor(model.getVendor().getName());
        modelType.setDescription(model.getDescription());
        modelType.setPrice(model.getPrice());
        modelType.setViews(model.getViews());
        phoneType.setModel(modelType);

        imageTypeList = model.getImageList().stream().map(image -> dozerBeanMapper.map(image, ImageType.class)).collect(Collectors.toList());
        phoneType.getImages().addAll(imageTypeList);

        detailTypeList = detailsMapper.map(detailList).stream().filter(detailDtoType -> detailDtoType.getDetailId() != ADDITIONAL_DETAIL_ID).map(detailDtoType -> dozerBeanMapper.map(detailDtoType, DetailType.class)).collect(Collectors.toList());
        detailTypeList = detailTypeList.stream().map(detailType -> this.getEnrichedDetailType(detailType, filterDetailValue(detailType, model))).collect(Collectors.toList());
        detailTypeList.addAll(detailTypeList);
        phoneType.getDetails().addAll(detailTypeList);
        return phoneType;
    }

    private DetailType getEnrichedDetailType(DetailType detailType, DetailValue detailValue) {
        detailType.setValue(detailValue.getValue());
        detailType.setUnit(detailValue.getUnit());
        return detailType;
    }

    private DetailValue filterDetailValue(DetailType detailType, Model model) {
        DetailValue resultDetailValue = new DetailValue();

        Optional<DetailValue> optionalDetailValue = model.getDetailValueList().stream().filter(detailValue -> detailValue.getDetail().getDetailId().toString().equals(detailType.getDetailId())).findFirst();
        if (optionalDetailValue.isPresent()) {
            resultDetailValue = optionalDetailValue.get();
        } else {
            log.warn("Не найдено значение характеристики '" + detailType.getName() +"' для '" + model.getName() + "'.");
        }

        return resultDetailValue;
    }
}
