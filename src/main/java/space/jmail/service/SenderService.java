package space.jmail.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import space.jmail.dao.SenderDao;
import space.jmail.entity.*;
import space.jmail.scheduler.Scheduler;
import space.jmail.util.Helper;
import space.jmail.validator.EmailValidator;

import java.text.ParseException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by kernel32 on 30.09.2017.
 */
@Service
public class SenderService extends Thread {

    @Autowired
    private MessageService messageService;
    @Autowired
    private RealReceiverService realReceiverService;
    @Autowired
    private FakeReceiverService fakeReceiverService;

    @Autowired
    private SenderDao senderDao;

    public SenderService(SenderDao senderDao) {
        this.senderDao = senderDao;
    }

    public SenderService() {
    }

    private static Logger log = Logger.getLogger("file");
    private static Logger sLog = Logger.getLogger("sentInbox");

    public void run() {
        log.info("<----- start scheduling for email sending/resetting ----->");
        while (!Thread.currentThread().isInterrupted()) {
            scheduleSenderReset();
            scheduleEmailSending();
        }
    }

    public void scheduleSenderReset() {
        Date date = Helper.getResetDate();
        Scheduler.resetSenderStatus(date);
        log.info("Schedule sender reset date: " + Helper.getFormattedDate(date));
    }

    public void scheduleEmailSending() {
        while (true) {
            List<Sender> senderList = senderDao.getWorkingSenderList();
            sendEmails(senderList);
        }
    }

    private void sendEmails(List<Sender> senderList) {
        Iterator<Sender> senderIterator = senderList.iterator();
        while (senderIterator.hasNext()) {
            sendEmail(senderIterator.next());
        }
    }

    private void sendEmail(Sender sender) {
        int maxLetters = sender.getMaxLetters();
        maxLetters = (maxLetters - Helper.getRandIntElement(1, 10)) / 2;
        for (int i = 0; i < maxLetters; i++) {
            makeScheduleForSending(sender);
        }
    }

    private void makeScheduleForSending(Sender sender) {
        RealReceiver realReceiver = realReceiverService.getFirstUnusedRandomRealReceiver();
        if(!isAddressValid(realReceiver.getEmail())){
            makeScheduleForSending(sender);
        }
        Message message = messageService.getFirstRandomMessage(ExistenceType.REAL);
        Date date = Helper.getRandomCurrentDateTime();

        if (message == null || realReceiver == null) {
            Helper.logMessageReceiverNullable(message, realReceiver);
            return;
        }

        try {
            Scheduler scheduler = new Scheduler();
            Helper.logRealEmailScheduling(sender, realReceiver, date, ExistenceType.REAL.name());
            scheduler.setRealEmailSending(sender, realReceiver, message, date);
            realReceiverService.setReceiverAsPlannedForPlanning(realReceiver);

            Date dateForFakeEmailSending = Helper.getDateForFakeEmailSending(date);
            FakeReceiver fReceiver = fakeReceiverService.getFirstRandomReceiver();
            Message fMessage = messageService.getFirstRandomMessage(ExistenceType.FAKE);
            Helper.logRealEmailScheduling(sender, realReceiver, dateForFakeEmailSending, ExistenceType.FAKE.name());
            scheduler.setFakeEmailSending(sender, fReceiver, fMessage, dateForFakeEmailSending);
        } catch (ParseException e) {
            e.printStackTrace();
            log.error("Error while scheduling email sending", e);
        }
    }

    private boolean isAddressValid(String email) {
        try {
            EmailValidator.checkAddressValidity(email);
        } catch (UnsupportedOperationException e){
            log.error("error while checking email: " + email, e);
            RealReceiver realReceiver = realReceiverService.getRealReceiverByEmail(email);
            realReceiverService.setNotSendReceiverStatus(realReceiver, e);
            return false;
        }
        return true;
    }

    public void send(Sender sender, Receiver receiver, Message message) {
        EmailService manager = new EmailService(sender, receiver, message);
        boolean isSent = manager.send();
        if (isSent) {
            String logText = "---------------------------\n";
            if (receiver instanceof RealReceiver) {
                realReceiverService.setReceiverStatus((RealReceiver) receiver, ItemStatus.PROCESSED);
                logText += "successfully sent:" + " from " + sender.getEmail() + " to real receiver " + receiver.getEmail() + " at " + Helper.getFormattedDate(new Date()) + "\n";
            } else {
                logText += "successfully sent:" + " from " + sender.getEmail() + " to fake receiver " + receiver.getEmail() + " at " + Helper.getFormattedDate(new Date()) + "\n";
            }
            logText += "---------------------------\n";
            senderDao.incrementSentLettersCount(sender);
            messageService.incrementSendCountOfMessage(message);
            sLog.info(logText);
        }
    }

    public void senderReset() {
        senderDao.resetSenderStatusDateSentLetters();
    }

    public List<Sender> getAllSenderList() {
        return senderDao.getAllSenderList();
    }

    public void setPlannedEmailsAsNotPlanned() {
        senderDao.setPlannedEmailsAsNotPlanned();
    }
}
