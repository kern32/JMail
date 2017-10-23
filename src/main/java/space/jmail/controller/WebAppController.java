package space.jmail.controller;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import space.jmail.entity.ExistenceType;
import space.jmail.scheduler.Scheduler;
import space.jmail.service.RealReceiverService;
import space.jmail.service.SenderService;
import space.jmail.service.StatService;
import space.jmail.starter.WebStarter;
import space.jmail.util.Helper;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
public class WebAppController {

    @Autowired
    private StatService statService;
    @Autowired
    private SenderService senderService;
    @Autowired
    private RealReceiverService realReceiverService;
    @Autowired
    Helper helper;

    private static Logger log = Logger.getLogger("file");

    @RequestMapping(value = {"/", "/index"}, method = GET)
    public String index()  {
        return "index";
    }

    @RequestMapping(value = "/get", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void getStatistic(HttpServletResponse response) throws IOException {
        HashMap JSONROOT = new HashMap();

        JSONROOT.put("senderList", statService.getSenderListForStatistic());
        JSONROOT.put("realReceiverList", statService.getRealReceiverListForStatistic());
        JSONROOT.put("fakeReceiverList", statService.getFakeReceiverListForStatistic());
        JSONROOT.put("sendingFlag", WebStarter.isSendingFlag());
        JSONROOT.put("resetDate", Scheduler.getRessetDate());
        JSONROOT.put("Result", "OK");

        Gson gson = new GsonBuilder()
                .setDateFormat("MMM dd, yyyy HH:mm:ss")
                .setPrettyPrinting()
                .create();

        String jsonArray = gson.toJson(JSONROOT);
        response.setContentType("json");
        response.getWriter().print(jsonArray);
    }

    @RequestMapping(value = "/start", method = POST)
    public String start() {
        WebStarter webStarter = new WebStarter();
        webStarter.setSenderService(senderService);
        webStarter.setReceiveService(realReceiverService);
        webStarter.start();
        return "index";
    }

    @RequestMapping(value = "/stop", method = POST)
    public String stop() {
        WebStarter webStarter = new WebStarter();
        webStarter.setSenderService(senderService);
        webStarter.setReceiveService(realReceiverService);
        webStarter.stop();
        return "index";
    }

    @RequestMapping(value = "/import", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void parseAndSaveReceivers(@RequestBody String data, HttpServletResponse response) throws IOException {
        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(data).getAsJsonObject();
        String rPath = json.get("real").getAsString();
        String fPath = json.get("fake").getAsString();

        if (!rPath.isEmpty()) {
            helper.parseAndSaveReceivers(rPath, ExistenceType.REAL);
        }
        if (!fPath.isEmpty()) {
            helper.parseAndSaveReceivers(fPath, ExistenceType.FAKE);
        }

        HashMap<String, Object> JSONROOT = new HashMap<String, Object>();
        JSONROOT.put("Result", "OK");

        com.google.gson.Gson gson = new GsonBuilder()
                .setDateFormat("MMM dd, yyyy HH:mm:ss")
                .setPrettyPrinting()
                .create();

        String jsonArray = gson.toJson(JSONROOT);
        response.setContentType("json");
        response.getWriter().print(jsonArray);
    }
}

