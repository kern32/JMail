package space.jmail.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import space.jmail.dao.RealReceiverDao;
import space.jmail.entity.Message;
import space.jmail.entity.Receiver;
import space.jmail.entity.Sender;

import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Service
public class EmailService {

    @Autowired
    private RealReceiverDao realReceiverDao;

    private static Logger log = Logger.getLogger("file");
    private static Logger nSlog = Logger.getLogger("notSentInbox");

    public EmailService(RealReceiverDao realReceiverDao){
        this.realReceiverDao = realReceiverDao;
    }
    public EmailService(){
    }

    Sender sender;
    Receiver receiver;
    Message message;
    Properties props;
    Session session;

    public EmailService(Sender sender, Receiver receiver, Message message) {
        manageSenderProps(sender);
        manageReceiverProps(receiver);
        manageMessageProps(message);
        manageProperties();
        manageSession();
    }

    private void manageReceiverProps(Receiver receiver) {
        this.receiver = receiver;
    }

    private void manageSenderProps(Sender sender) {
        this.sender = sender;
    }

    private void manageMessageProps(Message message) {
        this.message = message;
    }

    private void manageProperties() {
        props = new Properties();
        props.put("mail.smtp.host", sender.getSmtpServer());
        props.put("mail.smtp.socketFactory.port", sender.getSmtpPort());
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", sender.getSmtpPort());
    }

    private void manageSession() {
        session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(sender.getEmail(), sender.getPassword());
                    }
                });
    }

    public boolean send()  {
        try {
            javax.mail.Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(sender.getEmail()));
            message.setRecipients(javax.mail.Message.RecipientType.TO,
                    InternetAddress.parse(receiver.getEmail()));
            message.setSubject(this.message.getSubject());
            message.setText(generateMessage(this.message));
            Transport.send(message);
            return true;
        } catch (MessagingException e) {
            realReceiverDao.setNotSendReceiverStatus(receiver, e);
            String logText = "---------------------------\n";
            String error = "Error while sending email " + receiver.getEmail() + "\n";
            logText += error ;
            logText += "---------------------------\n";
            nSlog.error(error, e);
            return false;
        }
    }

    private String generateMessage(Message message) {
        return message.getBody();
    }
}
