import com.mongodb.rx.client.Success;
import dao.MongoDriver;
import model.Ticket;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import rx.observers.TestSubscriber;
import server.ManagerServer;
import services.EventNotificationService;

import java.util.*;

public class ManagerServerTest {

    private static final String DATABASE = "manager-server-test";
    private final static String TICKET_ID_FIELD = "ticket-id";
    private final static String DATE_FIELD = "date";

    private final static String TICKET_ID = "101";
    private final static String DATE = "12-04-2020";

    private final static int DAY = 12;
    private final static int MONTH = 4;
    private final static int YEAR = 2020;

    MongoDriver mongoDriver;
    ManagerServer server;

    @Before
    public void before() {
        TestSubscriber<Success> subscriber = new TestSubscriber<>();
        MongoDriver
                .MONGO_CLIENT
                .getDatabase(DATABASE)
                .drop()
                .subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        mongoDriver = new MongoDriver(MongoDriver.MONGO_CLIENT, DATABASE, new EventNotificationService());
        server = new ManagerServer(mongoDriver);
    }

    @Test
    public void testAddTicketVersions() throws Throwable {
        Map<String, List<String>> params = new HashMap<>();
        params.put(TICKET_ID_FIELD, Collections.singletonList(TICKET_ID));
        params.put(DATE_FIELD, Collections.singletonList(DATE));

        Date date = new Date();
        server.handleTicketOperation(params, date);

        List<Ticket> tickets = mongoDriver.getTicketVersions(Integer.parseInt(TICKET_ID));
        Assert.assertEquals(1, tickets.size());

        Ticket ticket = tickets.get(0);
        Assert.assertEquals(Integer.parseInt(TICKET_ID), ticket.getId());

        Assert.assertEquals(date, ticket.getStartDate());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(ticket.getExpirationDate());

        Assert.assertEquals(DAY, calendar.get(Calendar.DAY_OF_MONTH));
        Assert.assertEquals(MONTH, calendar.get(Calendar.MONTH) + 1);
        Assert.assertEquals(YEAR, calendar.get(Calendar.YEAR));
    }

    @Test
    public void testAddTicketLatestVersion() throws Throwable {
        Map<String, List<String>> params = new HashMap<>();
        params.put(TICKET_ID_FIELD, Collections.singletonList(TICKET_ID));
        params.put(DATE_FIELD, Collections.singletonList(DATE));

        Date date = new Date();
        server.handleTicketOperation(params, date);
        Ticket ticket = mongoDriver.getLatestTicket(Integer.parseInt(TICKET_ID));
        Assert.assertEquals(Integer.parseInt(TICKET_ID), ticket.getId());
        Assert.assertEquals(date, ticket.getStartDate());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(ticket.getExpirationDate());

        Assert.assertEquals(DAY, calendar.get(Calendar.DAY_OF_MONTH));
        Assert.assertEquals(MONTH, calendar.get(Calendar.MONTH) + 1);
        Assert.assertEquals(YEAR, calendar.get(Calendar.YEAR));
    }

    @Test
    public void testNecessaryParams() {
        Map<String, List<String>> params = new HashMap<>();

        String expectedResponse = "Please, add params: " + TICKET_ID_FIELD + ", " + DATE_FIELD;
        String actualResponse = server.handleTicketOperation(params, new Date()).toBlocking().first();

        Assert.assertEquals(expectedResponse, actualResponse);
    }
}
