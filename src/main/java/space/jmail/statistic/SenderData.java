package space.jmail.statistic;

import java.util.Date;

public class SenderData {
    private final String sender;
    private final String status;
    private final String processDate;
    private final int sentLetters;
    private final int maxLetters;

    public String getSender() {
        return sender;
    }

    public String getStatus() {
        return status;
    }

    public String getProcessDate() {
        return processDate;
    }

    public int getSentLetters() {
        return sentLetters;
    }

    public int getMaxLetters() {
        return maxLetters;
    }

    public SenderData(String sender, String status, String processDate, int sentLetters, int maxLetters) {
        this.sender = sender;
        this.status = status;
        this.processDate = processDate;
        this.sentLetters = sentLetters;
        this.maxLetters = maxLetters;
    }

    public static class Builder {
        private String sender;
        private String status;
        private String processDate;
        private int sentLetters;
        private int maxLetters;

        public Builder setSender(String sender) {
            this.sender = sender;
            return this;
        }

        public Builder setStatus(String status) {
            this.status = status;
            return this;
        }

        public Builder setProcessDate(String processDate) {
            this.processDate = processDate;
            return this;
        }

        public Builder setSentLetters(int sentLetters) {
            this.sentLetters = sentLetters;
            return this;
        }

        public Builder setMaxLetters(int maxLetters) {
            this.maxLetters = maxLetters;
            return this;
        }

        public SenderData build(){
            return new SenderData(sender, status, processDate,
                    sentLetters, maxLetters);
        }
    }
}
