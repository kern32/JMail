package space.jmail.entity;

import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table (name = "sender")
public class Sender {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column (name = "processed_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ItemStatus status;

    @Column (name = "email", nullable = false, unique = true)
    private String email;

    @Column (name = "process_date", nullable = false)
    private Date date;

    @Column (name = "sent_letters")
    @ColumnDefault("0")
    private int sent_letters;

    @Column (name = "max_letters")
    @ColumnDefault("500")
    private int maxLetters;

    @Column (name = "login", nullable = false)
    private String login;

    @Column (name = "password", nullable = false)
    private String password;

    @Column (name = "smtpServer", nullable = false)
    private String smtpServer;

    @Column (name = "smtpPort", nullable = false)
    private int smtpPort;

    @Column (name = "pop3Server", nullable = false)
    private String pop3Server;

    @Column (name = "pop3Port", nullable = false)
    private int pop3Port;

    @Column (name = "registration_date", nullable = false)
    private Date registrationDate;

    @Column (name = "expiration", nullable = false)
    private int expiration;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ItemStatus getStatus() {
        return status;
    }

    public void setStatus(ItemStatus status) {
        this.status = status;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getSent_letters() {
        return sent_letters;
    }

    public void setSent_letters(int sent_letters) {
        this.sent_letters = sent_letters;
    }

    public int getMaxLetters() {
        return maxLetters;
    }

    public void setMaxLetters(int maxLetters) {
        this.maxLetters = maxLetters;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSmtpServer() {
        return smtpServer;
    }

    public void setSmtpServer(String smtpServer) {
        this.smtpServer = smtpServer;
    }

    public int getSmtpPort() {
        return smtpPort;
    }

    public void setSmtpPort(int smtpPort) {
        this.smtpPort = smtpPort;
    }

    public String getPop3Server() {
        return pop3Server;
    }

    public void setPop3Server(String pop3Server) {
        this.pop3Server = pop3Server;
    }

    public int getPop3Port() {
        return pop3Port;
    }

    public void setPop3Port(int pop3Port) {
        this.pop3Port = pop3Port;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public int getExpiration() {
        return expiration;
    }

    public void setExpiration(int expiration) {
        this.expiration = expiration;
    }

    @Override
    public String toString() {
        return "Sender{" +
                "email='" + email + '\'' +
                '}';
    }
}
