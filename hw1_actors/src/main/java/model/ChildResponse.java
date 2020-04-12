package model;

import org.json.JSONArray;
import search.SearchEngines;

public class ChildResponse {

    private final SearchEngines searchEngine;
    private final String query;
    private final JSONArray results;

    public ChildResponse(SearchEngines searchEngine, String query, JSONArray results) {
        this.searchEngine = searchEngine;
        this.query = query;
        this.results = results;
    }

    public SearchEngines getSearchEngine() {
        return searchEngine;
    }

    public String getQuery() {
        return query;
    }

    public JSONArray getResults() {
        return results;
    }
}
