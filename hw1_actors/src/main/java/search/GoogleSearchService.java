package search;

import org.json.JSONArray;

import java.net.MalformedURLException;
import java.net.URL;

public class GoogleSearchService implements SearchService {

    private static final String HOST = "https://google.com";

    @Override
    public JSONArray search(StubServer server, String query) throws MalformedURLException {
        URL url = new URL(HOST + "/?q=" + query);
        return server.getResults(url);
    }
}
