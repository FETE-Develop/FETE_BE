package fete.be.domain;

public enum Status {
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
