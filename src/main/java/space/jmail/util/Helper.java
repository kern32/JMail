package space.jmail.util;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import space.jmail.entity.*;
import space.jmail.service.FakeReceiverService;
import space.jmail.service.RealReceiverService;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class Helper {

    private static Logger log = Logger.getLogger("file");

    @Autowired
    private RealReceiverService realReceiverService;
    @Autowired
    private FakeReceiverService fakeReceiverService;


    public static String getFormattedDate(Date date) {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String formattedDate = df.format(date);
        return formattedDate;
    }

    public static int getRandIntElement(int min, int max) {
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }

    public static Date getRandomCurrentDateTime() {
        Random rnd = new Random();
        Date randDate = new Date(Math.abs(System.currentTimeMillis() - rnd.nextLong()));

        DateTime dt = DateTime.now();
        Date currentDate = dt.toDate();
        dt = dt.withHourOfDay(getHoursBiggerThanNow(randDate, currentDate));
        dt = dt.withMinuteOfHour(getMinBiggerThanNow(randDate, currentDate));
        dt = dt.withSecondOfMinute(getSecBiggerThanNow(randDate, currentDate));
        Date date = dt.toDate();

        return date;
    }

    private static int getSecBiggerThanNow(Date randDate, Date currentDate) {
        int randSeconds = randDate.getSeconds();
        int currSeconds = currentDate.getSeconds();
        if(randSeconds >= currSeconds){
            return randSeconds;
        } else {
            randDate = new Date(Math.abs(System.currentTimeMillis() - new Random().nextLong()));
            return getSecBiggerThanNow(randDate, currentDate);
        }
    }

    private static int getMinBiggerThanNow(Date randDate, Date currentDate) {
        int randMinutes = randDate.getMinutes();
        int currMinutes = currentDate.getMinutes();
        if(randMinutes >= currMinutes){
            return randMinutes;
        } else {
            randDate = new Date(Math.abs(System.currentTimeMillis() - new Random().nextLong()));
            return getMinBiggerThanNow(randDate, currentDate);
        }
    }

    private static int getHoursBiggerThanNow(Date randDate, Date currentDate) {
        int randHours = randDate.getHours();
        int currHours = currentDate.getHours();
        if(randHours >= currHours){
            return randHours;
        } else {
            randDate = new Date(Math.abs(System.currentTimeMillis() - new Random().nextLong()));
            return getHoursBiggerThanNow(randDate, currentDate);
        }
    }

    public static Date getDateForFakeEmailSending(Date startDate) {
        Date nextRandomDate;
        int minutes = getRandomMinutesFrom6to15();
        DateTime dateTime = new DateTime(startDate);
        DateTime nextTime = dateTime.plusMinutes(minutes);

        if(dateTime.getDayOfMonth() == nextTime.getDayOfMonth()){
            nextRandomDate = nextTime.toDate();
        } else {
            nextRandomDate = nextTime.minusMinutes(minutes).toDate();
        }
        return nextRandomDate;
    }

    private static int getRandomMinutesFrom6to15() {
        List<Integer> list = new ArrayList<Integer>() {
            {
                add(6);
                add(7);
                add(8);
                add(9);
                add(10);
                add(11);
                add(12);
                add(13);
                add(14);
                add(15);
            }
        };
        Random randomizer = new Random();
        int random = list.get(randomizer.nextInt(list.size()));
        return random;
    }

    public static Date getResetDate() {
        DateTime dt = DateTime.now();
        dt = dt.withHourOfDay(00);
        dt = dt.withMinuteOfHour(00);
        dt = dt.withSecondOfMinute(00);
        dt = dt.plusDays(1);
        Date date = dt.toDate();
        return date;
    }

    public static void logRealEmailScheduling(Sender sender, Receiver receiver, Date date, String type) {
        log.info("Schedule email sending: " + type + " sender: " + sender.getEmail() + "; " +
                " receiver: " + receiver.getEmail() + "; " +
                " time: " + Helper.getFormattedDate(date));
    }

    public static void logMessageReceiverNullable(Message message, RealReceiver realReceiver) {
        String errMsg = "Error!! can't send email: ";
        if (message == null) {
            errMsg += "message is null; ";
        }
        if (realReceiver == null) {
            errMsg += "realReceiver is null";
        }
        log.error(errMsg);
    }

    public void parseAndSaveReceivers(String path, ExistenceType type) {
        File item = new File(path);
        if (item.isFile()) {
            parseReceiversFromFile(item, type);
        } else if (item.isDirectory()) {
            File[] files = item.listFiles();
            for (File file : files) {
                parseReceiversFromFile(file, type);
            }
        }
    }

    private void parseReceiversFromFile(File item, ExistenceType type) {
        if (item.getName().endsWith(".txt")) {
            saveReceiversFromFile(item, type);
        } else {
            log.error("Incorrect file extension: " + item.getName() + ". Should be *.txt");
        }
    }

    private void saveReceiversFromFile(File item, ExistenceType type) {
        FileReader input = getFileReader(item);
        BufferedReader bufRead = new BufferedReader(input);
        String line = null;

        int rowNum = 1;
        try {
            while ((line = bufRead.readLine()) != null) {
                rowNum++;
                String[] array = line.split(":");
                parseReceiverLine(array, item, rowNum, type);
            }
        } catch (IOException e) {
            log.error("Error while reading line number: " + rowNum + " of file " + item.getName());
        }
    }

    private FileReader getFileReader(File item) {
        FileReader input = null;
        try {
            input = new FileReader(item);
        } catch (FileNotFoundException e) {
            log.error("File not found: " + item.getName(), e);
        }
        return input;
    }

    private void parseReceiverLine(String[] array, File item, int rowNum, ExistenceType type) {
        if(type == ExistenceType.FAKE){
            saveNewFakeReceivers(array, item, rowNum);
        } else {
            saveNewRealReceivers(array, item, rowNum);
        }
    }

    private void saveNewRealReceivers(String[] array, File item, int rowNum) {
        if (array.length == 1){
            String email = array[0];
            realReceiverService.saveNewRealReceiver(email);
        } else {
            log.error("Error while parsing file: " + item.getName() + " in line: " + rowNum + ". Element size: " + array.length);
        }
    }

    private void saveNewFakeReceivers(String[] array, File item, int rowNum) {
        if(array.length == 2){
            String email = array[0];
            String pass = array[1];
            fakeReceiverService.saveNewFakeReceiver(email, pass);
        } else if (array.length == 1){
            String email = array[0];
            fakeReceiverService.saveNewFakeReceiver(email);
        } else {
            log.error("Error while parsing file: " + item.getName() + " in line: " + rowNum + ". Element size: " + array.length);
        }
    }
}
