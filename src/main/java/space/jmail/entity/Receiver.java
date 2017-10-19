package space.jmail.entity;


public class Receiver {

    private String email;
    private int id;
    private String exception;

    public String getEmail(){
        return this.email;
    };
    public int getId(){
        return this.id;
    };
    public void setException(String exception){
        this.exception = exception;
    }

    @Override
    public String toString() {
        return "Receiver{" +
                "email='" + email + '\'' +
                '}';
    }
}
