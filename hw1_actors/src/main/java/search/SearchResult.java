package search;

public class SearchResult {

    private final String title;
    private final String body;

    public SearchResult(String title, String body) {
        this.title = title;
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }
}
