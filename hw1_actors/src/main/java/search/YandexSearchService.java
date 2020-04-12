package search;

import org.json.JSONArray;

import java.net.MalformedURLException;
import java.net.URL;

public class YandexSearchService implements SearchService {

    public static final String YA_HOST = "https://yandex.ru";

    @Override
    public JSONArray search(StubServer server, String query) throws MalformedURLException {
        URL url = new URL(YA_HOST + "/?q=" + query);
        return server.getResults(url);
    }
}
