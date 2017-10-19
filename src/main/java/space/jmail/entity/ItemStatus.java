package space.jmail.entity;

public enum ItemStatus {
    PROCESSED(1), UNPROCESSED(0);

    int status;

    public int getStatus() {
        return status;
    }

    ItemStatus(int status){
        this.status = status;
    }
}
