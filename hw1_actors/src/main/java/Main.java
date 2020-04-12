import actors.AnswerHolder;
import actors.MasterActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import model.MasterRequest;
import search.StubServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        ActorSystem system = ActorSystem.create();

        String in;
        while ((in = bufferedReader.readLine()) != null) {
            if (in.equals("quit")) {
                system.terminate();
                System.exit(0);
            }
            StubServer server = new StubServer();
            AnswerHolder answerHolder = new AnswerHolder();
            ActorRef master = system.actorOf(
                    Props.create(MasterActor.class, server, answerHolder),
                    "master");
            master.tell(new MasterRequest(in), ActorRef.noSender());
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(answerHolder.getAnswer());
        }
    }
}
