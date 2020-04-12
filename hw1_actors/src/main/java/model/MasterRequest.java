package model;

public class MasterRequest {

    private final String query;

    public MasterRequest(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }
}
