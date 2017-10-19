package space.jmail.statistic;

public class FakeReceiverData {

    private final String receiveType;
    private final int successSent;
    private final int failedSent;

    public FakeReceiverData(String receiveType, int successSent, int failedSent) {
        this.receiveType = receiveType;
        this.successSent = successSent;
        this.failedSent = failedSent;
    }

    public String getReceiveType() {
        return receiveType;
    }

    public int getSuccessSent() {
        return successSent;
    }

    public int getFailedSent() {
        return failedSent;
    }

    public static class Builder {

        private String receiveType;
        private int successSent;
        private int failedSent;

        public Builder setReceiveType(String receiveType) {
            this.receiveType = receiveType;
            return this;
        }

        public Builder setSuccessSent(int successSent) {
            this.successSent = successSent;
            return this;
        }

        public Builder setFailedSent(int failedSent) {
            this.failedSent = failedSent;
            return this;
        }

        public FakeReceiverData build(){
            return new FakeReceiverData(receiveType, successSent, failedSent);
        }
    }
}
