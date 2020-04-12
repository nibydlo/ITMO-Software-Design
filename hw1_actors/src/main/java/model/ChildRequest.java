package model;

import search.SearchEngines;
import search.StubServer;

public class ChildRequest {

    private final SearchEngines searchEngine;
    private final String query;
    private StubServer server;

    public ChildRequest(StubServer server, SearchEngines searchEngine, String query) {
        this.server = server;
        this.searchEngine = searchEngine;
        this.query = query;
    }

    public SearchEngines getSearchEngine() {
        return searchEngine;
    }

    public String getQuery() {
        return query;
    }

    public StubServer getServer() {
        return server;
    }
}
