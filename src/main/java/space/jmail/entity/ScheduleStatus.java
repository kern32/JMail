package space.jmail.entity;

public enum ScheduleStatus {
    PLANNED(1), NOT_PLANNED(0);

    int status;

    public int getStatus() {
        return status;
    }

    ScheduleStatus(int status){
        this.status = status;
    }
}
