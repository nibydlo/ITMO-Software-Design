import com.mongodb.rx.client.Success;
import dao.MongoDriver;
import model.Event;
import model.Ticket;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import rx.observers.TestSubscriber;
import server.EntryServer;
import services.EventNotificationService;

import java.util.*;

import static model.Event.EventType.ENTER;
import static model.Event.EventType.EXIT;

public class EntryServerTest {

    private static final String DATABASE = "entry-server-test";
    private static final String TICKET_ID_FIELD = "ticket-id";

    MongoDriver mongoDriver;
    EntryServer server;

    @Before
    public void before() {
        TestSubscriber<Success> subscriber = new TestSubscriber<>();
        MongoDriver.MONGO_CLIENT.getDatabase(DATABASE).drop().subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        mongoDriver = new MongoDriver(MongoDriver.MONGO_CLIENT, DATABASE, new EventNotificationService());
        server = new EntryServer(mongoDriver);
    }

    @Test
    public void testAddEnter() throws Throwable {
        Map<String, List<String>> params = new HashMap<>();
        params.put(TICKET_ID_FIELD, Collections.singletonList("1"));

        Date expiry = new Date();
        expiry.setTime(expiry.getTime() + 10000);
        Date creation = new Date();
        mongoDriver.addTicket(new Ticket(1, expiry, creation));

        Date enter = new Date();
        enter.setTime(expiry.getTime() - 5000);
        server.addEnter(params, enter);
        List<Event> events = mongoDriver.getEvents();
        Assert.assertEquals(1, events.size());
        Event event = events.get(0);
        Assert.assertEquals(enter, event.getTime());
        Assert.assertEquals(ENTER, event.getEventType());
        Assert.assertEquals(1, event.getTicketId());
    }

    @Test
    public void testAddExit() throws Throwable {
        Map<String, List<String>> params = new HashMap<>();
        params.put(TICKET_ID_FIELD, Collections.singletonList("1"));

        Date expiry = new Date();
        expiry.setTime(expiry.getTime() + 10000);
        Date creation = new Date();
        mongoDriver.addTicket(new Ticket(1, expiry, creation));

        Date enter = new Date();
        enter.setTime(expiry.getTime() - 5000);
        server.addExit(params, enter);
        List<Event> events = mongoDriver.getEvents();
        Assert.assertEquals(1, events.size());
        Event event = events.get(0);
        Assert.assertEquals(enter, event.getTime());
        Assert.assertEquals(EXIT, event.getEventType());
        Assert.assertEquals(1, event.getTicketId());
    }

    @Test
    public void testNoTicketId() {
        Map<String, List<String>> params = new HashMap<>();
        Assert.assertEquals("No " + TICKET_ID_FIELD, server.addEnter(params, new Date()).toBlocking().first());
    }
}
