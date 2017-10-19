package space.jmail.dao;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import space.jmail.entity.Message;
import space.jmail.entity.ExistenceType;
import space.jmail.util.Helper;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by kernel32 on 01.10.2017.
 */
@Repository
@Transactional
public class MessageDao {

    private static Logger log = Logger.getLogger("file");

    @Autowired
    private SessionFactory sessionFactory;

    public MessageDao(SessionFactory sessionFactory){
        this.sessionFactory = sessionFactory;
    }

    public Message getFirstRandomMessage(ExistenceType type) {
        Message message = null;
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        Criteria c = session.createCriteria(Message.class)
                .add(Restrictions.eq("type", type));

        List<Message> messageList = c.list();
        int size = messageList.size();

        if (size > 1) {
            int num = Helper.getRandIntElement(1, size) - 1;
            message = messageList.get(num);
        } else if (size == 1) {
            message = messageList.get(0);
        }

        transaction.commit();
        session.close();
        log.info("return " + type.name() + " message with id: " + (message != null ? message.getId() : "null"));
        return message;
    }

    public void incrementSendCountOfMessage(Message message) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        message = session.get(Message.class, message.getId());
        int count = message.getCount() + 1;
        message.setCount(count);

        transaction.commit();
        session.close();
        log.info("set count: " + count + " to message with id: " + message.getId());
    }
}
