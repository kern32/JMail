package space.jmail.scheduler;

import org.apache.log4j.Logger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import space.jmail.entity.*;
import space.jmail.service.RealReceiverService;
import space.jmail.util.Helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;

/**
 * Created by kernel32 on 09.10.2017.
 */
public class Scheduler {

    private static Logger log = Logger.getLogger("file");
    private static Date ressetDate = null;
    private static HashSet<org.quartz.Scheduler> schedulerSet = new HashSet();

    public static String getRessetDate() {
        if(ressetDate != null){
            return new SimpleDateFormat().format(ressetDate);
        }
        return "reset date not initialized yet...";
    }

    public static void clearAllJobs(){
        for(org.quartz.Scheduler sch : schedulerSet){
            try {
                sch.shutdown();
            } catch (SchedulerException e) {
                handleClearJobException(sch, e);
            }
        }
    }

    private static void handleClearJobException(org.quartz.Scheduler sch, SchedulerException e) {
        try {
            log.error("job " + sch.getSchedulerName() + " stop error", e);
        } catch (SchedulerException ex) {
            log.error("getting job name error while stop job", ex);
        }
    }

    public void setRealEmailSending(Sender sender, RealReceiver realReceiver, Message message, Date startDate) throws ParseException {
        log.info("Schedule to real send email from " + sender.getEmail() + " to " + realReceiver.getEmail() + " at " + Helper.getFormattedDate(startDate));
        try {
            JobDetail messageDetail = new JobDetail();
            messageDetail.setName("Send real email JobDetail name: " + realReceiver.getEmail() + "; id:" + realReceiver.getId());
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put("sender", sender);
            jobDataMap.put("receiver", realReceiver);
            jobDataMap.put("message", message);

            messageDetail.setJobDataMap(jobDataMap);
            messageDetail.setJobClass(RealSendJobExecuter.class);

            SimpleTrigger trigger = new SimpleTrigger();
            trigger.setName("Real email sending trigger name: " + realReceiver.getEmail() + "; id:" + realReceiver.getId());
            trigger.setStartTime(startDate);

            org.quartz.Scheduler scheduler =  new StdSchedulerFactory().getScheduler();
            scheduler.start();
            scheduler.scheduleJob(messageDetail, trigger);

            schedulerSet.add(scheduler);
        } catch (SchedulerException e) {
            e.printStackTrace();
            log.error("RealSendJobExecuter: error while setting schedule for real sending email, full stack trace follows:", e);
        }
    }

    public static void resetSenderStatus(Date date){
        log.info("Schedule to reset sender status, sent letters count, date at " + Helper.getFormattedDate(date));
        try {
            JobDetail messageDetail = new JobDetail();
            messageDetail.setName("Reset sender JobDetail name: " + Helper.getFormattedDate(date));
            JobDataMap jobDataMap = new JobDataMap();

            messageDetail.setJobDataMap(jobDataMap);
            messageDetail.setJobClass(ResetJobExecuter.class);

            SimpleTrigger trigger = new SimpleTrigger();
            trigger.setName("Reset sender trigger name: " + Helper.getFormattedDate(date));
            trigger.setStartTime(date);

            org.quartz.Scheduler scheduler =  new StdSchedulerFactory().getScheduler();
            scheduler.start();
            scheduler.scheduleJob(messageDetail, trigger);
            ressetDate = date;

            schedulerSet.add(scheduler);
        } catch (SchedulerException e) {
            e.printStackTrace();
            log.error("ResetSenderJobExecuter: error while setting schedule for resetting sender status, full stack trace follows:", e);
        }
    }

    public void setFakeEmailSending(Sender sender, FakeReceiver fReceiver, Message fMessage, Date startDate) {
        log.info("Schedule to send fake email from " + sender.getEmail() + " to " + fReceiver.getEmail() + " at " + Helper.getFormattedDate(startDate));
        try {
            JobDetail messageDetail = new JobDetail();
            messageDetail.setName("Send fake email JobDetail name: " + fReceiver.getEmail() + "; id:" + fReceiver.getId());
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put("sender", sender);
            jobDataMap.put("receiver", fReceiver);
            jobDataMap.put("message", fMessage);

            messageDetail.setJobDataMap(jobDataMap);
            messageDetail.setJobClass(FakeSendJobExecuter.class);

            SimpleTrigger trigger = new SimpleTrigger();
            trigger.setName("Fake email sending trigger name: " + fReceiver.getEmail() + "; id:" + fReceiver.getId());
            trigger.setStartTime(startDate);

            org.quartz.Scheduler scheduler =  new StdSchedulerFactory().getScheduler();
            scheduler.start();
            scheduler.scheduleJob(messageDetail, trigger);

            schedulerSet.add(scheduler);
        } catch (SchedulerException e) {
            e.printStackTrace();
            log.error("FakeSendJobExecuter: error while setting schedule for fake sending email, full stack trace follows:", e);
        }
    }
}

