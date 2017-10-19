package space.jmail.starter;


import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import space.jmail.scheduler.Scheduler;
import space.jmail.service.RealReceiverService;
import space.jmail.service.SenderService;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;

@Service
public class WebStarter {

    private static Logger log = Logger.getLogger("file");
    private static boolean sendingFlag = false;

    private SenderService senderService;
    private RealReceiverService receiveService;

    public void setSenderService(SenderService senderService) {
        this.senderService = senderService;
    }

    public void setReceiveService(RealReceiverService receiveService) {
        this.receiveService = receiveService;
    }

    public static void main(String[] args) throws URISyntaxException, ParseException {
        /*initialization();
        start();*/
    }

    public void start()  {
        senderService.start();
        log.info("sending manager started...");
        receiveService.start();
        log.info("receive manager started...");
        sendingFlag = true;
    }

    public void stop() {
        if(senderService != null && senderService != null && !senderService.isInterrupted() && !senderService.isInterrupted()){
            senderService.interrupt();
            log.info("sending manager stopped...");
            senderService.interrupt();
            log.info("receive manager stopped...");
            Scheduler.clearAllJobs();
            senderService.setPlannedEmailsAsNotPlanned();
            sendingFlag = false;
        }
    }

    public static boolean isSendingFlag() {
        return sendingFlag;
    }

    @Deprecated
    private static void initialization() throws URISyntaxException {
        URL resourceUrl = WebStarter.class.getClassLoader().getResource("log4j.properties");
        String log4jlocation = resourceUrl.toURI().getPath();

        if (log4jlocation == null) {
            System.err.println("log4j properties not found. Start BasicConfigurator");
            BasicConfigurator.configure();
        } else {
            File log4jFile = new File(log4jlocation);

            if (log4jFile.exists()) {
                System.out.println("Configuring log4j from: " + log4jlocation);
                PropertyConfigurator.configure(log4jlocation);
            } else {
                System.err.println("Configuration file " + log4jlocation + " not found. Initialize with basic configurator.");
                BasicConfigurator.configure();
            }
        }
    }
}
