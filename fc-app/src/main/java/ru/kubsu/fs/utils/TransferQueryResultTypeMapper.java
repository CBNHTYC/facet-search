package ru.kubsu.fs.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.kubsu.fs.entity.DetailValue;
import ru.kubsu.fs.entity.Model;
import ru.kubsu.fs.schema.ResponseParameters.*;
import ru.kubsu.fs.model.DetailsEnum;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class TransferQueryResultTypeMapper {
//    public TransferQueryResultType map(List<Model> phoneList){
//        ObjectFactory objectFactory= new ObjectFactory();
//        TransferQueryResultType resultType = objectFactory.createTransferQueryResultType(); //todo переделать форыч в стрим
//        resultType.setHeader(objectFactory.createHeaderType());
//        PhoneListType phoneListType = objectFactory.createPhoneListType();
//        phoneList.forEach(phone -> {
//            PhoneType phoneType = new PhoneType();
//            phoneType.setImages(new ImagesType());
//            phoneType.setPrice(phone.getPrice());
//            phoneType.setModelName(phone.getName());
//
//            phoneType.setAccumulator(getArgumentFromDetailValue(DetailsEnum.ACCUMULATOR.getValue(), phone));
//            phoneType.setDiagonal(getArgumentFromDetailValue(DetailsEnum.DIAGONAL.getValue(), phone));
//            phoneType.setVendor(phone.getVendor().getName());
//            phoneType.setRam(getArgumentFromDetailValue(DetailsEnum.RAM.getValue(), phone));
//            phoneType.setSim(getArgumentFromDetailValue(DetailsEnum.SIM.getValue(), phone));
//            phoneType.setViews(phone.getViews());
//
//            ImageType imageType = new ImageType();
//            if (phone.getImageList() != null) {
//                phone.getImageList().forEach(image -> {
//                    imageType.setLocation(image.getLocation());
//                    phoneType.getImages().getImage().add(imageType);
//                });
//            }
//            phoneListType.getPhones().add(phoneType);
//        });
//        resultType.setPhoneList(phoneListType);
//        return resultType;
//    }

    private String getArgumentFromDetailValue(String detailName, Model model) {
        Optional<DetailValue> optDetailValue = model.getDetailValueList().stream().filter(detValue -> detValue.getDetail().getName().equals(detailName)).findFirst();
        if (optDetailValue.isPresent()){
            DetailValue detailValue = optDetailValue.get();
            return detailValue.getValue();
        }else {
            log.error("Не найден аттрибут: '" + detailName + "' для модели с id: " + model.getModelId());
            return "";
        }
    }
}
