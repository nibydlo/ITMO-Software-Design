package actors;

import akka.actor.UntypedActor;
import model.ChildRequest;
import model.ChildResponse;
import org.json.JSONArray;
import search.*;

public class ChildActor extends UntypedActor {

    private final static String WRONG_ENGINE_MESSAGE = "Unexpected search engine: ";

    @Override
    public void onReceive(Object o) throws Throwable {
        if (o instanceof ChildRequest) {
            ChildRequest request = (ChildRequest) o;
            StubServer server = request.getServer();
            SearchEngines searchEngine = request.getSearchEngine();
            String query = request.getQuery();
            SearchService searchService;
            switch (searchEngine) {
                case BING:
                    searchService = new BingSearchService();
                    break;
                case GOOGLE:
                    searchService = new GoogleSearchService();
                    break;
                case YANDEX:
                    searchService = new YandexSearchService();
                    break;
                default:
                    throw new IllegalArgumentException(WRONG_ENGINE_MESSAGE + searchEngine);
            }
            JSONArray results = searchService.search(server, query);
            ChildResponse response = new ChildResponse(searchEngine, query, results);
            getSender().tell(response, getSelf());
        } else {
            unhandled(o);
        }
    }
}
