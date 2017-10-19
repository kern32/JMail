package space.jmail.entity;


import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@Entity
@Table(name = "fake_receiver")
public class FakeReceiver extends Receiver{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "count")
    @ColumnDefault("0")
    private int count;

    @Column(name = "exception")
    private String exception;

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getException() {
        return exception;
    }

    @Override
    public void setException(String exception) {
        this.exception = exception;
    }
}
