package space.jmail.scheduler;

import org.apache.log4j.Logger;
import org.quartz.*;
import space.jmail.entity.FakeReceiver;
import space.jmail.entity.Message;
import space.jmail.entity.Sender;
import space.jmail.service.SenderService;

public class FakeSendJobExecuter implements Job {
    private static Logger log = Logger.getLogger("file");

    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("start fake sending job...");
        JobDetail messageDetail = context.getJobDetail();
        JobDataMap jobDataMap = messageDetail.getJobDataMap();

        Sender sender = (Sender) jobDataMap.get("sender");
        FakeReceiver receiver = (FakeReceiver) jobDataMap.get("receiver");
        Message message = (Message) jobDataMap.get("message");

        SenderService senderService = new SenderService();
        senderService.send(sender, receiver, message);
    }
}
