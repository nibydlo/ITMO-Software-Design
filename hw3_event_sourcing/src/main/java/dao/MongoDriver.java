package dao;

import com.mongodb.rx.client.MongoClient;
import com.mongodb.rx.client.MongoClients;
import com.mongodb.rx.client.Success;
import model.Event;
import model.Ticket;
import org.bson.Document;
import rx.Subscriber;
import services.EventNotificationService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static com.mongodb.client.model.Filters.eq;

public class MongoDriver {

    public static final MongoClient MONGO_CLIENT = MongoClients.create("mongodb://localhost:27017");
    private final static String EVENTS_COLLECTION = "events";
    private final static String TICKETS_COLLECTION = "tickets";
    private final static String ID_FIELD = "id";
    private final static Integer TIMEOUT = 10;
    private final String database;
    private final MongoClient mongoClient;
    private final EventNotificationService notificationService;

    public MongoDriver(MongoClient mongoClient, String database, EventNotificationService notificationService) {
        this.mongoClient = mongoClient;
        this.database = database;
        this.notificationService = notificationService;
    }

    public Success addTicket(Ticket ticket) {
        return mongoClient
                .getDatabase(database)
                .getCollection(TICKETS_COLLECTION)
                .insertOne(ticket.toDocument())
                .timeout(TIMEOUT, TimeUnit.SECONDS)
                .toBlocking()
                .single();
    }

    public Ticket getLatestTicket(Integer id) throws Throwable {
        List<Ticket> tickets = getTicketVersions(id);
        Ticket latestTicket = null;
        for (Ticket ticket : tickets) {
            if (latestTicket == null) {
                latestTicket = ticket;
            } else if (ticket.getStartDate().after(latestTicket.getStartDate())) {
                latestTicket = ticket;
            }
        }
        return latestTicket;
    }

    public List<Ticket> getTicketVersions(Integer id) throws Throwable {
        ArrayList<Ticket> tickets = new ArrayList<>();
        ArrayList<Throwable> errors = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(1);

        mongoClient
                .getDatabase(database)
                .getCollection(TICKETS_COLLECTION)
                .find(eq(ID_FIELD, id))
                .maxTime(TIMEOUT, TimeUnit.SECONDS)
                .subscribe(new Subscriber<Document>() {
                    @Override
                    public void onCompleted() {
                        latch.countDown();
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        errors.add(throwable);
                        latch.countDown();
                    }

                    @Override
                    public void onNext(Document document) {
                        tickets.add(new Ticket(document));
                    }
                });

        latch.await();
        if (errors.isEmpty()) {
            return tickets;
        }
        throw errors.get(0);
    }

    public Success addEvent(Event event) {
        Success result = mongoClient
                .getDatabase(database)
                .getCollection(EVENTS_COLLECTION)
                .insertOne(event.toDocument())
                .timeout(TIMEOUT, TimeUnit.SECONDS)
                .toBlocking()
                .single();
        if (result == Success.SUCCESS) {
            notificationService.addEvent(event);
        }
        return result;
    }

    public List<Event> getEvents() throws Throwable {
        List<Event> events = new ArrayList<>();
        List<Throwable> errors = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(1);

        mongoClient
                .getDatabase(database)
                .getCollection(EVENTS_COLLECTION)
                .find()
                .maxTime(TIMEOUT, TimeUnit.SECONDS)
                .subscribe(new Subscriber<Document>() {
                    @Override
                    public void onCompleted() {
                        latch.countDown();
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        errors.add(throwable);
                        latch.countDown();
                    }

                    @Override
                    public void onNext(Document document) {
                        events.add(new Event(document));
                    }
                });

        latch.await();
        if (errors.isEmpty()) {
            return events;
        }
        throw errors.get(0);
    }
}
