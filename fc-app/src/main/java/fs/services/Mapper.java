package fs.services;

import fs.entity.Phone;
import org.springframework.stereotype.Service;
import ru.kubsu.fs.schema.ResponseParameters.*;

import java.util.List;

@Service
public class Mapper {
    public TransferQueryResultType map(List<Phone> phoneList){
        TransferQueryResultType resultType = new TransferQueryResultType(); //todo переделать форыч в стрим
        resultType.setHeader(new HeaderType());
        resultType.setPhoneList(new PhoneListType());
        phoneList.forEach(phone -> {
            PhoneType phoneType = new PhoneType();
            phoneType.setComments(new CommentsType());
            phoneType.setImages(new ImagesType());
            phoneType.setPrice(phone.getPrice());
            phoneType.setModel(phone.getModel());
            phoneType.setAccumulator(phone.getAccumulator());
            phoneType.setDiagonal(phone.getDiagonal());
            phoneType.setProducer(phone.getProducer());
            phoneType.setRam(phone.getRam());
            phoneType.setSim(phone.getSim());
            phoneType.setViews(phone.getViews());

            CommentType commentType = new CommentType();
            if(phone.getPhoneComments() != null) {
                phone.getPhoneComments().forEach(comment -> {
                    commentType.setDate(comment.getDate().toString());
                    commentType.setDegree(comment.getDegree());
                    commentType.setText(comment.getText());
                    commentType.setUsername(comment.getUsername());
                    phoneType.getComments().getComment().add(commentType);
                });
            }

            ImageType imageType = new ImageType();
            if (phone.getPhoneImages() != null) {
                phone.getPhoneImages().forEach(image -> {
                    imageType.setLocation(image.getLocation());
                    phoneType.getImages().getImage().add(imageType);
                });
            }
            resultType.getPhoneList().getPhone().add(phoneType);
        });
        return resultType;
    }
}
