package space.jmail.dao;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import space.jmail.entity.*;

import javax.transaction.Transactional;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by kernel32 on 01.10.2017.
 */
@Repository
@Transactional
public class RealReceiverDao {
    private static Logger log = Logger.getLogger("file");

    @Autowired
    private SessionFactory sessionFactory;

    public RealReceiverDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public RealReceiver getFirstUnusedRandomRealReceiver() {
        RealReceiver realReceiver = null;
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        Criteria c = session.createCriteria(RealReceiver.class)
                .add(Restrictions.eq("status", ItemStatus.UNPROCESSED))
                .add(Restrictions.eq("scheduleStatus", ScheduleStatus.NOT_PLANNED))
                .setFirstResult(0)
                .setMaxResults(1);

        List<RealReceiver> realReceiverList = c.list();
        if (realReceiverList.size() > 0) {
            realReceiver = realReceiverList.get(0);
        }

        transaction.commit();
        session.close();
        log.info("return real receiver: " + (realReceiver != null ? realReceiver.getEmail() : "null"));
        return realReceiver;
    }

    public RealReceiver getRealReceiverByEmail(String email) {
        RealReceiver realReceiver = null;
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        Criteria c = session.createCriteria(RealReceiver.class)
                .add(Restrictions.eq("email", email));

        List<RealReceiver> realReceiverList = c.list();
        if (realReceiverList.size() == 1) {
            realReceiver = realReceiverList.get(0);
        }

        transaction.commit();
        session.close();
        log.info("return realReceiver :" + (realReceiver != null ? realReceiver.getEmail() : "null"));
        return realReceiver;
    }

    public void setNotSendReceiverStatus(Receiver receiver, Exception e) {
        String logText = "";
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        if (receiver instanceof RealReceiver) {
            RealReceiver rReceiver = session.get(RealReceiver.class, receiver.getId());
            rReceiver.setStatus(ItemStatus.PROCESSED);
            receiver.setException(e.toString());
            logText = "sending message failed to real receiver: " + receiver.getEmail() + " with exception " + e;
        } else {
            FakeReceiver fReceiver = session.get(FakeReceiver.class, receiver.getId());
            fReceiver.setException(e.toString());
            logText = "sending message failed to fake receiver: " + receiver.getEmail() + " with exception " + e;
        }

        transaction.commit();
        session.close();
        log.info(logText);
    }

    public void setReceiverStatus(RealReceiver receiver, ItemStatus status) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        receiver = session.get(RealReceiver.class, receiver.getId());
        receiver.setStatus(status);

        transaction.commit();
        session.close();
        log.info("update realReceiver " + receiver.getEmail() + " with status " + status.name());
    }

    public void updateRealReceiverAsBroken(Set<String> emailList, String reason) {
        Iterator<String> iterator = emailList.iterator();
        while (iterator.hasNext()) {
            RealReceiver realReceiver = getRealReceiverByEmail(iterator.next());
            if (realReceiver != null) {
                setNotSendReceiverStatus(realReceiver, new Exception(reason));
            }
        }
    }

    public void setReceiverAsPlannedForSending(RealReceiver realReceiver) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        realReceiver = session.get(RealReceiver.class, realReceiver.getId());
        realReceiver.setScheduleStatus(ScheduleStatus.PLANNED);

        transaction.commit();
        session.close();
        log.info("update real receiver " + realReceiver.getEmail() + " for next sending with schedule_status " + ScheduleStatus.PLANNED.name());
    }

    public void saveNewRealReceiver(String email) {
        try {
            Session session = sessionFactory.openSession();
            Transaction transaction = session.beginTransaction();

            RealReceiver rReceiver = new RealReceiver();
            rReceiver.setEmail(email);
            rReceiver.setScheduleStatus(ScheduleStatus.NOT_PLANNED);
            rReceiver.setStatus(ItemStatus.UNPROCESSED);
            session.save(rReceiver);

            transaction.commit();
            session.close();
        } catch (Exception e) {
            log.error("Error while saving real email receiver to db: " + email, e);
        }
    }
}
