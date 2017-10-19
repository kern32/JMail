package space.jmail.statistic;

public class RealReceiverData {

    private final String receiveType;
    private final int successSent;
    private final int failedSent;
    private final int notExisting;
    private final int planned;
    private final int notPlanned;
    private final int totalProcessed;
    private final int totalUnProcessed;

    public RealReceiverData(String receiveType, int successSent, int failedSent, int notExisting, int planned, int notPlanned, int totalProcessed, int totalUnProcessed) {
        this.receiveType = receiveType;
        this.successSent = successSent;
        this.failedSent = failedSent;
        this.notExisting = notExisting;
        this.planned = planned;
        this.notPlanned = notPlanned;
        this.totalProcessed = totalProcessed;
        this.totalUnProcessed = totalUnProcessed;
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

    public int getPlanned() {
        return planned;
    }

    public int getNotPlanned() {
        return notPlanned;
    }

    public int getTotalProcessed() {
        return totalProcessed;
    }

    public int getTotalUnProcessed() {
        return totalUnProcessed;
    }

    public int getNotExisting() {
        return notExisting;
    }

    public static class Builder {

        private String receiveType;
        private int successSent;
        private int failedSent;
        private int notExisting;
        private int planned;
        private int notPlanned;
        private int totalProcessed;
        private int totalUnProcessed;

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

        public Builder setPlanned(int planned) {
            this.planned = planned;
            return this;
        }

        public Builder setNotPlanned(int notPlanned) {
            this.notPlanned = notPlanned;
            return this;
        }

        public Builder setTotalProcessed(int totalProcessed) {
            this.totalProcessed = totalProcessed;
            return this;
        }

        public Builder setTotalUnProcessed(int totalUnProcessed) {
            this.totalUnProcessed = totalUnProcessed;
            return this;
        }

        public Builder setNotExisting(int notExisting) {
            this.notExisting = notExisting;
            return this;
        }

        public RealReceiverData build(){
            return new RealReceiverData(receiveType, successSent, failedSent,
                    notExisting, planned, notPlanned,
                    totalProcessed, totalUnProcessed);
        }
    }
}
