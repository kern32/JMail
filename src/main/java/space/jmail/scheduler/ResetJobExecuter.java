package space.jmail.scheduler;

import org.apache.log4j.Logger;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import space.jmail.dao.SenderDao;
import space.jmail.service.SenderService;

/**
 * Created by kernel32 on 10.10.2017.
 */
public class ResetJobExecuter implements Job {

    @Autowired
    SenderService senderService;

    private static Logger log = Logger.getLogger("file");

    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("start sender resetting job...");
        senderService.senderReset();
        senderService.scheduleSenderReset();
    }
}