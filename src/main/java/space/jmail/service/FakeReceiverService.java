package space.jmail.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import space.jmail.dao.FakeReceiverDao;
import space.jmail.entity.FakeReceiver;

@Service
public class FakeReceiverService {

    @Autowired
    private FakeReceiverDao fakeReceiverDao;

    public FakeReceiverService(FakeReceiverDao fakeReceiverDao){
        this.fakeReceiverDao = fakeReceiverDao;
    }

    public FakeReceiver getFirstRandomReceiver() {
        return fakeReceiverDao.getFirstRandomReceiver();
    }

    public void saveNewFakeReceiver(String email, String pass) {
        fakeReceiverDao.saveNewFakeReceiver(email, pass);
    }

    public void saveNewFakeReceiver(String email) {
        fakeReceiverDao.saveNewFakeReceiver(email);
    }
}
