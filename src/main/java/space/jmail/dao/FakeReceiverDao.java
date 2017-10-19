package space.jmail.dao;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import space.jmail.entity.FakeReceiver;
import space.jmail.entity.ItemStatus;
import space.jmail.entity.RealReceiver;
import space.jmail.entity.ScheduleStatus;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public class FakeReceiverDao {

    private static Logger log = Logger.getLogger("file");

    @Autowired
    private SessionFactory sessionFactory;

    public FakeReceiverDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public FakeReceiver getFirstRandomReceiver() {
        FakeReceiver fReceiver = null;
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        Criteria c = session.createCriteria(FakeReceiver.class)
                .add(Restrictions.isNull("exception"))
                .setFirstResult(0)
                .setMaxResults(1)
                .addOrder(Order.asc("count"));

        List<FakeReceiver> fList = c.list();
        if (fList.size() == 1) {
            fReceiver = fList.get(0);
        }

        transaction.commit();
        session.close();
        log.info("return fake receiver: " + (fReceiver != null ? fReceiver.getEmail() : "null"));
        return fReceiver;
    }

    public void saveNewFakeReceiver(String email, String pass) {
        try {
            Session session = sessionFactory.openSession();
            Transaction transaction = session.beginTransaction();

            FakeReceiver fReceiver = new FakeReceiver();
            fReceiver.setEmail(email);
            fReceiver.setPassword(pass);
            fReceiver.setCount(0);
            session.save(fReceiver);

            transaction.commit();
            session.close();
        } catch (Exception e) {
            log.error("Error while saving fake email receiver to db: " + email, e);
        }
    }

    public void saveNewFakeReceiver(String email) {
        try {
            Session session = sessionFactory.openSession();
            Transaction transaction = session.beginTransaction();

            FakeReceiver fReceiver = new FakeReceiver();
            fReceiver.setEmail(email);
            fReceiver.setCount(0);
            session.save(fReceiver);

            transaction.commit();
            session.close();
        } catch (Exception e) {
            log.error("Error while saving fake email receiver to db: " + email, e);
        }
    }
}
