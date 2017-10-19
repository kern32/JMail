package space.jmail.entity;

import javax.persistence.*;

@Entity
@Table(name = "real_receiver")
public class RealReceiver extends Receiver{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "schedule_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ScheduleStatus scheduleStatus;

    @Column(name = "processed_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ItemStatus status;

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

    public ScheduleStatus getScheduleStatus() {
        return scheduleStatus;
    }

    public void setScheduleStatus(ScheduleStatus scheduleStatus) {
        this.scheduleStatus = scheduleStatus;
    }

    public ItemStatus getStatus() {
        return status;
    }

    public void setStatus(ItemStatus status) {
        this.status = status;
    }

    public String getException() {
        return exception;
    }

    @Override
    public void setException(String exception) {
        this.exception = exception;
    }
}
