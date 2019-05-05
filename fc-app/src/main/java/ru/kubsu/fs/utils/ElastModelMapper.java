package ru.kubsu.fs.utils;

import org.springframework.stereotype.Component;
import ru.kubsu.fs.entity.*;
import ru.kubsu.fs.model.DetailsEnum;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ElastModelMapper {

    public List<ElastModel> map(List<Model> modelList) {
        return modelList.stream().map(this::map).collect(Collectors.toList());
    }

    public ElastModel map(Model model) {
        ElastModel elastModel = new ElastModel();
        elastModel.setModelId(String.valueOf(model.getModelId()));
        elastModel.setModelName(model.getName());
        elastModel.setVendor(model.getVendor().getName());
        elastModel.setDescription(model.getDescription());
        elastModel.setPrice(model.getPrice());
        elastModel.setViews(model.getViews());

        model.getDetailValueList().stream().filter(detailValue -> DetailsEnum.TYPE.getValue().getRu().equals(detailValue.getDetail().getName())).findFirst().ifPresent(detailValue -> elastModel.setType(detailValue.getValue()));
        model.getDetailValueList().stream().filter(detailValue -> DetailsEnum.ACCUMULATOR.getValue().getRu().equals(detailValue.getDetail().getName())).findFirst().ifPresent(detailValue -> elastModel.setAccumulator(detailValue.getValue()));
        model.getDetailValueList().stream().filter(detailValue -> DetailsEnum.DIAGONAL.getValue().getRu().equals(detailValue.getDetail().getName())).findFirst().ifPresent(detailValue -> elastModel.setDiagonal(detailValue.getValue()));
        model.getDetailValueList().stream().filter(detailValue -> DetailsEnum.RAM.getValue().getRu().equals(detailValue.getDetail().getName())).findFirst().ifPresent(detailValue -> elastModel.setRam(detailValue.getValue()));
        model.getDetailValueList().stream().filter(detailValue -> DetailsEnum.SIM.getValue().getRu().equals(detailValue.getDetail().getName())).findFirst().ifPresent(detailValue -> elastModel.setSim(detailValue.getValue()));
        elastModel.setImageLocationList(model.getImageList().stream().map(Image::getLocation).collect(Collectors.toList()));

        return elastModel;
    }
}
