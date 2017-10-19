package space.jmail.entity;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@Entity
@Table(name = "message")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name="type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ExistenceType type;

    @Column(name="author", nullable = false)
    private String author;

    @Column(name="subject", nullable = false)
    private String subject;

    @Column(name="body", nullable = false)
    private String body;

    @Column(name="count")
    @ColumnDefault("0")
    private int count;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ExistenceType getType() {
        return type;
    }

    public void setType(ExistenceType type) {
        this.type = type;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                '}';
    }
}
