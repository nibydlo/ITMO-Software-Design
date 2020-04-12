package search;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.List;


public class StubServer {

    private final int responseDelay;
    private List<String> urlsToDelay;

    public StubServer() {
        this.responseDelay = 0;
        this.urlsToDelay = List.of();
    }

    public StubServer(int responseDelay, List<String> urlsToDelay) {
        this.responseDelay = responseDelay;
        this.urlsToDelay = urlsToDelay;
    }

    public JSONArray getResults(URL url) {

        if (urlsToDelay.contains(url.toString())) {
            try {
                Thread.sleep(this.responseDelay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        JSONArray results = new JSONArray();

        for (int i = 0; i < 4; i++) {
            SearchResult result = new SearchResult("title" + i, "body" + i);
            JSONObject jsonObject = new JSONObject(result);
            results.put(jsonObject);
        }
        return results;
    }
}
