package space.jmail.scheduler;

import org.apache.log4j.Logger;
import org.quartz.*;
import space.jmail.entity.Message;
import space.jmail.entity.RealReceiver;
import space.jmail.entity.Sender;
import space.jmail.service.SenderService;

/**
 * Created by kernel32 on 09.10.2017.
 */
public class RealSendJobExecuter implements Job {

    private static Logger log = Logger.getLogger("file");

    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("start sending job...");
        JobDetail messageDetail = context.getJobDetail();
        JobDataMap jobDataMap = messageDetail.getJobDataMap();

        Sender sender = (Sender) jobDataMap.get("sender");
        RealReceiver realReceiver = (RealReceiver) jobDataMap.get("receiver");
        Message message = (Message) jobDataMap.get("message");

        SenderService senderService = new SenderService();
        senderService.send(sender, realReceiver, message);
    }
}
