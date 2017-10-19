package space.jmail.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import space.jmail.dao.RealReceiverDao;
import space.jmail.entity.ItemStatus;
import space.jmail.entity.RealReceiver;
import space.jmail.entity.Sender;

import javax.mail.*;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by kernel32 on 10.10.2017.
 */

@Service
public class RealReceiverService extends Thread {

    private static Logger log = Logger.getLogger("file");
    private static Logger dLog = Logger.getLogger("deletedInbox");

    @Autowired
    private RealReceiverDao realReceiverDao;
    @Autowired
    private SenderService senderService;

    public RealReceiverService(RealReceiverDao realReceiverDao){
        this.realReceiverDao = realReceiverDao;
    }

    public RealReceiverService(){
    }

    public void run() {
        log.info("<-----start receiving emails ----->");
        while (!Thread.currentThread().isInterrupted()) {

            try {
                //sleep for 5 minutes
                Thread.sleep(300000);
            } catch (InterruptedException e) {
                log.error("error while waiting before receiving new email", e);
                e.printStackTrace();
            }

            List<Sender> senderList = senderService.getAllSenderList();
            receiveAllEmail(senderList);
        }
    }

    private void receiveAllEmail(List<Sender> senderList) {
        Iterator<Sender> iter = senderList.iterator();
        while (iter.hasNext()) {
            receiveEmail(iter.next());
        }
    }

    public void receiveEmail(Sender sender) {
        try {
            Properties properties = new Properties();
            properties.put("mail.store.protocol", "pop3");
            properties.put("mail.pop3s.host", sender.getPop3Server());
            properties.put("mail.pop3s.port", sender.getPop3Port());
            properties.put("mail.pop3.starttls.enable", "true");
            Session emailSession = Session.getDefaultInstance(properties);
            // emailSession.setDebug(true);

            Store store = emailSession.getStore("pop3s");
            store.connect(sender.getPop3Server(), sender.getLogin(), sender.getPassword());

            Folder emailFolder = store.getFolder("INBOX");
            emailFolder.open(Folder.READ_WRITE);

            Message[] messages = emailFolder.getMessages();
            if (messages.length > 0) {
                markExtractedEmailAsBroken(messages, sender);
            }

            emailFolder.close(true);
            store.close();
        } catch (NoSuchProviderException e) {
            log.error("error while receiving email of " + sender.getEmail(), e);
            e.printStackTrace();
        } catch (MessagingException e) {
            log.error("error while receiving email of " + sender.getEmail(), e);
            e.printStackTrace();
        } catch (IOException io) {
            log.error("error while receiving email of " + sender.getEmail(), io);
            io.printStackTrace();
        }
    }

    private void markExtractedEmailAsBroken(Message[] messages, Sender sender) throws MessagingException, IOException {
        dLog.info("-----------------------------------");
        dLog.info("Income of " + sender.getEmail() + "\n");
        for (int i = 0; i < messages.length; i++) {
            Message message = messages[i];

            String logText = "Email Number " + (i + 1) + "\n";
            logText += "Subject: " + message.getSubject() + "\n";
            logText += "From: " + message.getFrom()[0] + "\n";

            String reason = extractReason(message);
            if(reason == null){
                reason = "non-receivable email. probably not exists";
            }
            Set<String> emailList = extractEmail(message);
            realReceiverDao.updateRealReceiverAsBroken(emailList, reason);

            message.setFlag(Flags.Flag.DELETED, true);
            logText += "Marked DELETE for message: " + message.getSubject() + "\n";
            dLog.info(logText);
        }
        dLog.info("-----------------------------------");
    }

    private String extractReason(Message message) throws IOException, MessagingException {
        String reason = null;
        Multipart multipart = (Multipart) message.getContent();
        for (int j = 0; j < multipart.getCount(); j++) {
            BodyPart bodyPart = multipart.getBodyPart(j);
            String content = bodyPart.getContent().toString();
            if(content.contains("Host or domain name not found")){
                return "Host or domain name not found";
            }
        }
        return reason;
    }

    private Set<String> extractEmail(Message message) throws IOException, MessagingException {
        Set<String> emailList = new HashSet<>();
        Multipart multipart = (Multipart) message.getContent();
        for (int j = 0; j < multipart.getCount(); j++) {
            BodyPart bodyPart = multipart.getBodyPart(j);
            String content = bodyPart.getContent().toString();

            Matcher m = Pattern.compile("[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+").matcher(content);
            while (m.find()) {
                String group = m.group();
                if (!emailList.contains(group)) {
                    dLog.info("Body of email: " + bodyPart.getContent());
                    dLog.info("Added email to db: " + group + " as non-receivable");
                    emailList.add(group);
                }
            }
        }
        return emailList;
    }

    public void setReceiverAsPlannedForPlanning(RealReceiver realReceiver) {
        realReceiverDao.setReceiverAsPlannedForSending(realReceiver);
    }

    public RealReceiver getFirstUnusedRandomRealReceiver() {
        return realReceiverDao.getFirstUnusedRandomRealReceiver();
    }

    public void setReceiverStatus(RealReceiver receiver, ItemStatus processed) {
        realReceiverDao.setReceiverStatus(receiver, processed);
    }

    public RealReceiver getRealReceiverByEmail(String email) {
        return realReceiverDao.getRealReceiverByEmail(email);
    }

    public void setNotSendReceiverStatus(RealReceiver realReceiver, UnsupportedOperationException e) {
        realReceiverDao.setNotSendReceiverStatus(realReceiver, e);
    }

    public void saveNewRealReceiver(String email) {
        realReceiverDao.saveNewRealReceiver(email);
    }
}


