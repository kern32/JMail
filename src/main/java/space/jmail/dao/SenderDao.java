package space.jmail.dao;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import space.jmail.entity.ItemStatus;
import space.jmail.entity.ScheduleStatus;
import space.jmail.entity.Sender;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by kernel32 on 01.10.2017.
 */
@Repository
@Transactional
public class SenderDao {

    private static Logger log = Logger.getLogger("file");

    @Autowired
    private SessionFactory sessionFactory;

    public SenderDao(SessionFactory sessionFactory){
        this.sessionFactory = sessionFactory;
    }

    public List<Sender> getAllSenderList() {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        Criteria c = session.createCriteria(Sender.class)
                .add(Restrictions.sqlRestriction("registration_date >= DATE_SUB(DATE_FORMAT(now(), \"%Y-%m-%d\"), INTERVAL expiration DAY)"));;
        List<Sender> senderList = c.list();

        transaction.commit();
        session.close();
        log.info("return all sender list: " + senderList);
        return senderList;
    }

    public List<Sender> getWorkingSenderList() {
        java.sql.Date sqlDate = new java.sql.Date(new java.util.Date().getTime());
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        Criteria c = session.createCriteria(Sender.class)
                .add(Restrictions.eq("status", ItemStatus.UNPROCESSED))
                .add(Restrictions.le("date", sqlDate))
                .add(Restrictions.sqlRestriction("sent_letters <= max_letters"))
                .add(Restrictions.sqlRestriction("registration_date >= DATE_SUB(DATE_FORMAT(now(), \"%Y-%m-%d\"), INTERVAL expiration DAY)"));
        List<Sender> senderList = c.list();

        transaction.commit();
        session.close();
        log.info("return sender list: " + senderList);
        return senderList;
    }

    public void incrementSentLettersCount(Sender sender) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        sender = session.get(Sender.class, sender.getId());
        int sent_letters = sender.getSent_letters() + 1;
        sender.setSent_letters(sent_letters);

        boolean flag = false;
        int max_letters = sender.getMaxLetters();
        String email = sender.getEmail();
        if ( max_letters <= sent_letters){
            sender.setStatus(ItemStatus.PROCESSED);
            flag = true;
        }

        transaction.commit();
        session.close();
        if(flag){
            log.info("update sender: " + "sent letters " + sent_letters +
                    " when max letters " + max_letters + "; set status as " + ItemStatus.PROCESSED.name() + " to sender " + email);
        } else {
            log.info("update sender: " + "sent letters " + sent_letters + " from sender " + email);
        }
    }

    public void resetSenderStatusDateSentLetters() {
        java.sql.Date sqlDate = new java.sql.Date(new java.util.Date().getTime());
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        String hqlUpdate = "update Sender c set c.processed_status = :unProcessedStatus, c.process_date = :date," +
                "c.sent_letters = :sent_letters where c.processed_status = :processedStatus" +
                " and sent_letters >= max_letters";

        int updatedEntities = session.createSQLQuery( hqlUpdate )
                .setString( "unProcessedStatus", ItemStatus.UNPROCESSED.name() )
                .setDate( "date", sqlDate)
                .setString( "sent_letters", "0" )
                .setString( "processedStatus", ItemStatus.PROCESSED.name() )
                .executeUpdate();
        transaction.commit();
        session.close();
        log.info("reset " + updatedEntities + " senders to status = " + ItemStatus.UNPROCESSED.name() +
                "; sent letters = 0; date = " + sqlDate.toString());
    }

    public void setPlannedEmailsAsNotPlanned() {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        String hqlUpdate = "update real_receiver r set r.schedule_status = 'NOT_PLANNED' " +
                "where r.processed_status = 'UNPROCESSED' and r.exception is null";

        int updatedEntities = session.createSQLQuery( hqlUpdate )
                .executeUpdate();
        transaction.commit();
        session.close();
        log.info("set " + updatedEntities + " receivers to schedule status = " + ScheduleStatus.NOT_PLANNED.name() +
                " which wasn't PROCESSED yet");
    }
}
