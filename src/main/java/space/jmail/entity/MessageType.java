package space.jmail.entity;

public enum MessageType {
    FAKE("F"), REAL("R");

    String type;

    public String getType() {
        return type;
    }

    MessageType(String type){
        this.type = type;
    }
}