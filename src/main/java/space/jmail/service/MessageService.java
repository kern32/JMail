package space.jmail.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import space.jmail.dao.MessageDao;
import space.jmail.entity.Message;
import space.jmail.entity.ExistenceType;

@Service
public class MessageService {

    @Autowired
    private MessageDao messageDao;

    public MessageService(MessageDao messageDao){
        this.messageDao = messageDao;
    }

    public Message getFirstRandomMessage(ExistenceType type) {
        return messageDao.getFirstRandomMessage(type);
    }

    public void incrementSendCountOfMessage(Message message) {
        messageDao.incrementSendCountOfMessage(message);
    }
}
