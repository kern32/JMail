package space.jmail.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import space.jmail.statistic.FakeReceiverData;
import space.jmail.statistic.RealReceiverData;
import space.jmail.statistic.SenderData;
import space.jmail.util.Helper;

import javax.transaction.Transactional;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Repository
@Transactional
public class StatisticDao {

    @Autowired
    private SessionFactory sessionFactory;

    public StatisticDao(SessionFactory sessionFactory){
        this.sessionFactory = sessionFactory;
    }

    public List<SenderData> getSenderList() {
        List<SenderData> senderList = new ArrayList<>();
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        Query query = session.createSQLQuery("select s.email as sender, " +
                "s.`processed_status` as status, " +
                "s.process_date as processDate, " +
                "s.sent_letters as sentLetters, " +
                "s.max_letters as maxLetters from sender s");
        List list = query.list();
        transaction.commit();
        session.close();

        Iterator iterator = list.iterator();
        while (iterator.hasNext()){
            Object[] next = (Object[]) iterator.next();
            SenderData senderData = new SenderData.Builder()
                    .setSender(next[0].toString())
                    .setStatus(next[1].toString())
                    .setProcessDate(Helper.getFormattedDate((Date)next[2]))
                    .setSentLetters(Integer.valueOf(next[3].toString()))
                    .setMaxLetters(Integer.valueOf(next[4].toString()))
                    .build();

            senderList.add(senderData);
        }

        return senderList;
    }

    public List<FakeReceiverData> getFakeReceiverList() {
        List<FakeReceiverData> fakeReceiverList = new ArrayList<>();
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        String hqlSelect = "select 'Fake' as receiveType, " +
                "(select sum(count) from fake_receiver where exception is null) as 'successSent', " +
                "(select sum(count) from fake_receiver r where r.exception is not null) as 'failedSent' " +
                "from dual";

        Query query = session.createSQLQuery(hqlSelect);
        List list = query.list();
        transaction.commit();
        session.close();

        Iterator iterator = list.iterator();
        while (iterator.hasNext()){
            Object[] next = (Object[]) iterator.next();
            FakeReceiverData fReceiveData = new FakeReceiverData.Builder()
                    .setReceiveType(next[0].toString())
                    .setSuccessSent(next[1] == null ? 0 : Integer.valueOf(next[1].toString()))
                    .setFailedSent(next[2] == null ? 0 : Integer.valueOf(next[2].toString()))
                    .build();
            fakeReceiverList.add(fReceiveData);
        }

        return fakeReceiverList;
    }

    public List<RealReceiverData> getRealReceiverList() {
        List<RealReceiverData> realReceiverList = new ArrayList<>();
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        String hqlSelect = "select 'Real' as receiveType," +
                "(select count(*) from real_receiver r where r.processed_status = 'PROCESSED' and r.exception is null) as 'successSent'," +
                "(select count(*) from real_receiver r where r.processed_status = 'PROCESSED' and r.exception is not null) as 'failedSent'," +
                "(select count(*) from real_receiver r where r.exception = 'non-existing email') as 'notExisting'," +
                "(select count(*) from real_receiver r where r.processed_status = 'UNPROCESSED' and r.schedule_status = 'PLANNED') as 'planned'," +
                "(select count(*) from real_receiver r where r.processed_status = 'UNPROCESSED' and r.schedule_status = 'NOT_PLANNED') as 'notPlanned'," +
                "(select count(*) from real_receiver r where r.processed_status = 'PROCESSED') as 'totalProcessed'," +
                "(select count(*) from real_receiver r where r.processed_status = 'UNPROCESSED') as 'totalUnProcessed'" +
                "from dual";

        Query query = session.createSQLQuery(hqlSelect);
        List list = query.list();
        transaction.commit();
        session.close();

        Iterator iterator = list.iterator();
        while (iterator.hasNext()){
            Object[] next = (Object[]) iterator.next();
            RealReceiverData rReceiveData = new RealReceiverData.Builder()
                    .setReceiveType(next[0].toString())
                    .setSuccessSent(next[1] == null ? 0 : Integer.valueOf(next[1].toString()))
                    .setFailedSent(next[2] == null ? 0 : Integer.valueOf(next[2].toString()))
                    .setNotExisting(next[3] == null ? 0 : Integer.valueOf(next[3].toString()))
                    .setPlanned(next[4] == null ? 0 : Integer.valueOf(next[4].toString()))
                    .setNotPlanned(next[5] == null ? 0 : Integer.valueOf(next[5].toString()))
                    .setTotalProcessed(next[6] == null ? 0 : Integer.valueOf(next[6].toString()))
                    .setTotalUnProcessed(next[7] == null ? 0 : Integer.valueOf(next[7].toString()))
                    .build();

            realReceiverList.add(rReceiveData);
        }
        return realReceiverList;
    }
}
