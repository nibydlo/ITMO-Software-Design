package search;

import org.json.JSONArray;

import java.net.MalformedURLException;
import java.net.URL;

public class BingSearchService implements SearchService {

    private static final String HOST = "https://bing.com";

    public JSONArray search(StubServer server, String query) throws MalformedURLException {
        URL url = new URL(HOST + "/?q=" + query);
        return server.getResults(url);
    }
}
