import actors.AnswerHolder;
import actors.MasterActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import model.ChildResponse;
import model.MasterRequest;
import model.MasterResponse;
import org.json.JSONArray;
import org.junit.Assert;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import search.SearchEngines;
import search.StubServer;

import java.util.List;

public class ActorsTest {

    private final static String TEST_QUERY = "TEST_QUERY";
    private final static JSONArray CHILD_RESPONSE = new JSONArray("[{\"title\":\"title0\",\"body\":\"body0\"}," +
            "{\"title\":\"title1\",\"body\":\"body1\"}," +
            "{\"title\":\"title2\",\"body\":\"body2\"}," +
            "{\"title\":\"title3\",\"body\":\"body3\"}]");
    private final static String TIMEOUT_MESSAGE = "Master actor receive time is out.";
    private final static String MASTER_ACTOR_NAME = "master";
    private final static List<String> URLS_TO_DELAY = List.of("https://bing.com/?q=TEST_QUERY");

    private final static Integer SLEEP_TIME = 10000;
    private final static Integer DELAY_TIME = 15000;

    @Test
    public void testNormal() {
        ActorSystem system = ActorSystem.create();
        AnswerHolder answerHolder = new AnswerHolder();
        StubServer server = new StubServer();
        ActorRef master = system.actorOf(Props.create(MasterActor.class, server, answerHolder), MASTER_ACTOR_NAME);

        master.tell(new MasterRequest(TEST_QUERY), ActorRef.noSender());
        try {
            Thread.sleep(SLEEP_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        MasterResponse response = answerHolder.getAnswer();

        system.terminate();

        Assert.assertNull(response.getError());
        Assert.assertEquals(SearchEngines.values().length, response.getChildRespons().size());
        for (ChildResponse childResponse : response.getChildRespons()) {
            Assert.assertEquals(TEST_QUERY, childResponse.getQuery());
            if (childResponse.getSearchEngine().equals(SearchEngines.BING)) {
                JSONAssert.assertEquals(CHILD_RESPONSE, childResponse.getResults(), false);
            } else if (childResponse.getSearchEngine().equals(SearchEngines.GOOGLE)) {
                JSONAssert.assertEquals(CHILD_RESPONSE, childResponse.getResults(), false);
            } else if (childResponse.getSearchEngine().equals(SearchEngines.YANDEX)) {
                JSONAssert.assertEquals(CHILD_RESPONSE, childResponse.getResults(), false);
            } else {
                Assert.fail();
            }
        }
    }

    @Test
    public void testTimeout() {
        ActorSystem system = ActorSystem.create();
        AnswerHolder answerHolder = new AnswerHolder();

        StubServer server = new StubServer(DELAY_TIME, URLS_TO_DELAY);
        ActorRef master = system.actorOf(Props.create(MasterActor.class, server, answerHolder), MASTER_ACTOR_NAME);
        master.tell(new MasterRequest(TEST_QUERY), ActorRef.noSender());
        try {
            Thread.sleep(SLEEP_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        MasterResponse response = answerHolder.getAnswer();
        system.terminate();

        Assert.assertEquals(TIMEOUT_MESSAGE, response.getError());
        Assert.assertEquals(SearchEngines.values().length - URLS_TO_DELAY.size(), response.getChildRespons().size());
        for (ChildResponse childResponse : response.getChildRespons()) {
            Assert.assertEquals(TEST_QUERY, childResponse.getQuery());
            if (childResponse.getSearchEngine().equals(SearchEngines.BING)) {
                Assert.fail();
            } else if (childResponse.getSearchEngine().equals(SearchEngines.GOOGLE)) {
                JSONAssert.assertEquals(CHILD_RESPONSE, childResponse.getResults(), false);
            } else if (childResponse.getSearchEngine().equals(SearchEngines.YANDEX)) {
                JSONAssert.assertEquals(CHILD_RESPONSE, childResponse.getResults(), false);
            } else {
                Assert.fail();
            }
        }
    }
}
