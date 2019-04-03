package fs.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import ru.kubsu.fs.model.ReplicationMessage;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class MailService {

//    @Value("${replication.recipient}")
//    private String replicationRecipient;
//
//    @Autowired
//    private JavaMailSender mailSender;
//
//    public void sendReplicationMessages(List<ReplicationMessage> messages) {
//        if ((messages == null) || messages.isEmpty()) return;
//
//        String replicationMessage = messages.stream()
//                .map(msg -> String.format(msg.getStatus().getTemplate(),
//                        msg.getFullName() +
//                                ", №: " + msg.getSerialNumber() +
//                                ", Дата последнего изменения в 1C: " + msg.getEventDate(),
//                        msg.getMessage()))
//                .collect(Collectors.joining(System.lineSeparator()));
//        send(replicationMessage, "1C AD репликция, Дата запуска: " + LocalDate.now(), replicationRecipient);
//    }
//
//    public void send(String text, String subject, String recipient) {
//        log.debug("send text {} to recipient {}", text, recipient);
//        try {
//            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
//            simpleMailMessage.setFrom("no_reply@utair.com");
//            simpleMailMessage.setSubject(subject);
//            simpleMailMessage.setTo(recipient);
//            simpleMailMessage.setText(text);
//            mailSender.send(simpleMailMessage);
//        } catch (MailException exception) {
//            exception.printStackTrace();
//        }
//    }
//
//    public void sendMessageWithAttachment(String to,
//                                          String subject,
//                                          String text,
//                                          String pathToAttachment) {
//        try {
//            MimeMessage message = mailSender.createMimeMessage();
//            MimeMessageHelper helper = new MimeMessageHelper(message, true);
//
//            helper.setTo(to);
//            helper.setSubject(subject);
//            helper.setText(text);
//
//            FileSystemResource file = new FileSystemResource(new File(pathToAttachment));
//            String filename = file.getFilename();
//            helper.addAttachment(filename, file);
//
//            mailSender.send(message);
//        } catch (MessagingException e) {
//            e.printStackTrace();
//        }
//    }
}
