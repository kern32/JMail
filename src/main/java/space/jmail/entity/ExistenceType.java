package space.jmail.entity;

public enum ExistenceType {
    FAKE("F"), REAL("R");

    String type;

    public String getType() {
        return type;
    }

    ExistenceType(String type){
        this.type = type;
    }
}