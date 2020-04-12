package actors;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.ReceiveTimeout;
import akka.actor.UntypedActor;
import akka.routing.RoundRobinPool;
import model.ChildRequest;
import model.ChildResponse;
import model.MasterRequest;
import model.MasterResponse;
import scala.concurrent.duration.Duration;
import search.SearchEngines;
import search.StubServer;

import java.util.ArrayList;
import java.util.List;


public class MasterActor extends UntypedActor {

    public static final int SEARCH_ENGINES_COUNT = SearchEngines.values().length;

    private final ActorRef childRouter;
    private List<ChildResponse> responses;
    private AnswerHolder answerHolder;
    private StubServer server;

    public MasterActor(StubServer server, AnswerHolder answerHolder) {
        this.server = server;
        this.answerHolder = answerHolder;
        childRouter = getContext().actorOf(
                new RoundRobinPool(SEARCH_ENGINES_COUNT).props(Props.create(ChildActor.class)),
                "childRouter"
        );
        responses = new ArrayList<>();
        getContext().setReceiveTimeout(Duration.create("7 seconds"));
    }

    @Override
    public void onReceive(Object o) {
        if (o instanceof MasterRequest) {
            MasterRequest request = (MasterRequest) o;
            for (int i = 0; i < SEARCH_ENGINES_COUNT; i++) {
                childRouter.tell(
                        new ChildRequest(
                                this.server,
                                SearchEngines.values()[i],
                                request.getQuery()),
                        getSelf()
                );
            }
        } else if (o instanceof ChildResponse) {
            ChildResponse response = (ChildResponse) o;
            responses.add(response);
            if (responses.size() == SEARCH_ENGINES_COUNT) {
                answerHolder.setAnswer(new MasterResponse(responses));
                context().stop(self());
            }
        } else if (o instanceof ReceiveTimeout) {
            MasterResponse response = new MasterResponse(responses);
            response.setError("Master actor receive time is out.");
            answerHolder.setAnswer(response);
            context().stop(self());
            getContext().system().terminate();
        } else {
            unhandled(o);
        }
    }

}
