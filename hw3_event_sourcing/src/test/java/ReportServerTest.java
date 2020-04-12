import com.mongodb.rx.client.Success;
import dao.MongoDriver;
import model.Event;
import model.Ticket;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import rx.observers.TestSubscriber;
import server.ReportServer;
import services.EventNotificationService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static model.Event.EventType.ENTER;
import static model.Event.EventType.EXIT;

public class ReportServerTest {

    private static final String DATABASE = "report-server-test";

    private static final int SEC = 1000;
    private static final int MIN = 60 * SEC;
    private static final int HOUR = 60 * MIN;
    private static final int DAY = 24 * HOUR;

    private static final String DATE_PATTERN = "dd-M-yyyy hh:mm:ss";
    private static final String START_DATE = "1-4-2020 5:00:00";
    private static final String EXPIRATION_DATE = "12-4-2020 5:00:00";
//    private static final String

    MongoDriver mongoDriver;
    ReportServer server;

    @Before
    public void before() throws Throwable {
        TestSubscriber<Success> subscriber = new TestSubscriber<>();
        MongoDriver.MONGO_CLIENT.getDatabase(DATABASE).drop().subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        EventNotificationService eventNotificationService = new EventNotificationService();
        mongoDriver = new MongoDriver(MongoDriver.MONGO_CLIENT, DATABASE, eventNotificationService);
        server = new ReportServer(mongoDriver, eventNotificationService);
    }

    @Test
    public void testStats() throws ParseException {
        fillEnters();
        Assert.assertEquals("12 4 2020: 2\n" +
                "13 4 2020: 1\n", server.getStats());
    }

    @Test
    public void testMedianLength() throws ParseException {
        fillEnters();
        Assert.assertEquals("Average training length is 1 hours, 50 minutes", server.getAverageLength());
    }

    private void fillEnters() throws ParseException {
        Date startDate = (new SimpleDateFormat(DATE_PATTERN)).parse(START_DATE);
        Date expirationDate = (new SimpleDateFormat(DATE_PATTERN)).parse(EXPIRATION_DATE);

        mongoDriver.addTicket(new Ticket(1, startDate, expirationDate));

        Date enter = new Date();
        enter.setTime(expirationDate.getTime() - 5000);
        mongoDriver.addEvent(new Event(1, enter, ENTER));

        Date exit = new Date();
        exit.setTime(enter.getTime() + 2 * HOUR);
        mongoDriver.addEvent(new Event(1, exit, EXIT));

        Date enter1 = new Date();
        enter1.setTime(exit.getTime() + 8 * HOUR + 30 * MIN + 20 * SEC);
        mongoDriver.addEvent(new Event(1, enter1, ENTER));

        Date exit1 = new Date();
        exit1.setTime(enter1.getTime() + HOUR + 20 * MIN);
        mongoDriver.addEvent(new Event(1, exit1, EXIT));

        Date enter2 = new Date();
        enter2.setTime(exit1.getTime() + DAY);
        mongoDriver.addEvent(new Event(2, enter2, ENTER));

        Date exit3 = new Date();
        exit3.setTime(enter2.getTime() + 2 * HOUR + 10 * MIN);
        mongoDriver.addEvent(new Event(2, exit3, EXIT));
    }
}
