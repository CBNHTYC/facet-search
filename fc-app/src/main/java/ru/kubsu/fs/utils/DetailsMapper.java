package ru.kubsu.fs.utils;

import lombok.extern.slf4j.Slf4j;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.kubsu.fs.dto.details.DetailDto;
import ru.kubsu.fs.entity.Detail;
import ru.kubsu.fs.model.DetailsEnum;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class DetailsMapper {
    @Autowired
    private DozerBeanMapper dozerBeanMapper;

    public List<DetailDto> map(List<Detail> detailList) {

        List<DetailDto> dtoTypeList = detailList.stream().map(detail -> dozerBeanMapper.map(detail, DetailDto.class)).collect(Collectors.toList());
        dtoTypeList = dtoTypeList.stream().filter(detailDtoType -> DetailsEnum.getRuNames().stream().anyMatch(detailDtoType.getName()::equals)).collect(Collectors.toList());
        this.addAdditionalDetails(dtoTypeList);

        return dtoTypeList;
    }

    private void addAdditionalDetails( List<DetailDto> dtoTypeList) {
        DetailDto detailDto = new DetailDto();
        detailDto.setDetailId(0);
        detailDto.setParentDetailId(0);
        detailDto.setName("Производитель");
        dtoTypeList.add(detailDto);

        detailDto = new DetailDto();
        detailDto.setDetailId(0);
        detailDto.setParentDetailId(0);
        detailDto.setName("Модель");
        dtoTypeList.add(detailDto);
    }
}
