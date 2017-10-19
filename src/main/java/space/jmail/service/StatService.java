package space.jmail.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import space.jmail.dao.StatisticDao;
import space.jmail.statistic.FakeReceiverData;
import space.jmail.statistic.RealReceiverData;
import space.jmail.statistic.SenderData;

import java.util.List;

@Service
public class StatService {

    @Autowired
    private StatisticDao statisticDao;

    public StatService(StatisticDao statisticDao) {
        this.statisticDao = statisticDao;
    }

    public List<SenderData> getSenderListForStatistic() {
        List<SenderData> senderList = statisticDao.getSenderList();
        return senderList;
    }

    public List<RealReceiverData> getRealReceiverListForStatistic() {
        List<RealReceiverData> realReceiverList = statisticDao.getRealReceiverList();
        return realReceiverList;
    }

    public List<FakeReceiverData> getFakeReceiverListForStatistic() {
        List<FakeReceiverData> fakeReceiverList = statisticDao.getFakeReceiverList();
        return fakeReceiverList;
    }
}
