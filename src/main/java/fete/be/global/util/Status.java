package fete.be.global.util;

public enum Status {
    WAIT("WAIT"),
    ACTIVE("ACTIVE"),
    DELETE("DELETE"),
    REPORT("REPORT");

    private String status;

    Status(String status) {
        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }
}
