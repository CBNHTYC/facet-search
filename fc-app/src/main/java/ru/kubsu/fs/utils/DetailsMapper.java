package ru.kubsu.fs.utils;

import lombok.extern.slf4j.Slf4j;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.kubsu.fs.entity.Detail;
import ru.kubsu.fs.model.DetailsEnum;
import ru.kubsu.fs.schema.DetailsDto.DetailDtoType;
import ru.kubsu.fs.schema.DetailsDto.ObjectFactory;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class DetailsMapper {
    @Autowired
    private DozerBeanMapper dozerBeanMapper;

    public List<DetailDtoType> map(List<Detail> detailList) {

        List<DetailDtoType> dtoTypeList = detailList.stream().map(detail -> dozerBeanMapper.map(detail, DetailDtoType.class)).collect(Collectors.toList());
        dtoTypeList = dtoTypeList.stream().filter(detailDtoType -> DetailsEnum.getRuNames().stream().anyMatch(detailDtoType.getName()::equals)).collect(Collectors.toList());
        this.addAdditionalDetails(dtoTypeList);

        return dtoTypeList;
    }

    private void addAdditionalDetails( List<DetailDtoType> dtoTypeList) {
        ObjectFactory objectFactory = new ObjectFactory();

        DetailDtoType detailDtoType = objectFactory.createDetailDtoType();
        detailDtoType.setDetailId(0);
        detailDtoType.setParentDetailId(0);
        detailDtoType.setName("Производитель");
        dtoTypeList.add(detailDtoType);

        detailDtoType = objectFactory.createDetailDtoType();
        detailDtoType.setDetailId(0);
        detailDtoType.setParentDetailId(0);
        detailDtoType.setName("Модель");
        dtoTypeList.add(detailDtoType);
    }
}
