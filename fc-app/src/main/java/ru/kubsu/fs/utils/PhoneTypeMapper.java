package ru.kubsu.fs.utils;

import lombok.extern.slf4j.Slf4j;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.kubsu.fs.entity.Model;
import ru.kubsu.fs.model.DetailsEnum;
import ru.kubsu.fs.schema.ResponseParameters.ImageType;
import ru.kubsu.fs.schema.ResponseParameters.ModelType;
import ru.kubsu.fs.schema.ResponseParameters.ObjectFactory;
import ru.kubsu.fs.schema.ResponseParameters.PhoneType;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class PhoneTypeMapper {
    @Autowired
    private DozerBeanMapper dozerBeanMapper;


    public PhoneType map(Model model) {
        ObjectFactory objectFactory = new ObjectFactory();

        PhoneType phoneType = objectFactory.createPhoneType();
        List<ImageType> imageTypeList;

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

        phoneType.getDetails().setAccumulator(model.getDetailValueList().stream().filter(detailValue -> DetailsEnum.ACCUMULATOR.getValue().getRu().equals(detailValue.getDetail().getName())).findFirst().get().getValue());
        phoneType.getDetails().setDiagonal(model.getDetailValueList().stream().filter(detailValue -> DetailsEnum.DIAGONAL.getValue().getRu().equals(detailValue.getDetail().getName())).findFirst().get().getValue());
        phoneType.getDetails().setRam(model.getDetailValueList().stream().filter(detailValue -> DetailsEnum.RAM.getValue().getRu().equals(detailValue.getDetail().getName())).findFirst().get().getValue());
        phoneType.getDetails().setSim(model.getDetailValueList().stream().filter(detailValue -> DetailsEnum.SIM.getValue().getRu().equals(detailValue.getDetail().getName())).findFirst().get().getValue());

        return phoneType;
    }
}
