import dao.ReactiveMongoDriver;
import server.Server;

public class Application {
    public static void main(String[] args) {
        Server server = new Server(new ReactiveMongoDriver());
        server.run();
    }
}
