package search;

import org.json.JSONArray;

import java.net.MalformedURLException;

public interface SearchService {

    JSONArray search(StubServer server, String query) throws MalformedURLException;
}
