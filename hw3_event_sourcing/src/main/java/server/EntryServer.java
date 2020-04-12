package server;

import com.mongodb.rx.client.Success;
import dao.MongoDriver;
import io.reactivex.netty.protocol.http.server.HttpServer;
import model.Event;
import model.Ticket;
import rx.Observable;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class EntryServer {

    private final static int PORT = 8080;
    private final static String TICKET_ID_FIELD = "ticket-id";
    private final static String ENTER_METHOD = "enter";
    private final static String EXIT_METHOD = "exit";
    private final static String NEW_ENTER_EVENT_MESSAGE = "New enter event";
    private final static String NEW_EXIT_EVENT_MESSAGE = "New exit event";
    private final static String UNKNOWN_COMMAND_MESSAGE = "Unknown command";
    private final static String ERROR_MESSAGE = "Error";
    private final static String EXCEPTION_MESSAGE = "Exception occured with message: ";
    private final static String NO_TICKET_ID_MESSAGE = "No " + TICKET_ID_FIELD + " param";
    private final static String TICKET_EXPIRED_MESSAGE = "Ticket expired";
    private final static String NO_TICKETS_MESSAGE = "No tickets found";
    private final MongoDriver mongoDriver;

    public EntryServer(MongoDriver mongoDriver) {
        this.mongoDriver = mongoDriver;
    }

    public void run() {
        HttpServer.newServer(PORT).start((request, response) -> {
            String command = request.getDecodedPath().substring(1);
            Map<String, List<String>> params = request.getQueryParameters();

            if (ENTER_METHOD.equals(command)) {
                return response.writeString(addEnter(params, new Date()));
            }

            if (EXIT_METHOD.equals(command)) {
                return response.writeString(addExit(params, new Date()));
            }

            return response.writeString(Observable.just(UNKNOWN_COMMAND_MESSAGE));
        }).awaitShutdown();
    }

    public Observable<String> addEnter(Map<String, List<String>> params, Date date) {
        String validation = validate(params);

        if (validation.length() > 0) {
            return Observable.just(validation);
        }

        int id = Integer.parseInt(params.get(TICKET_ID_FIELD).get(0));
        Ticket ticket;

        try {
            ticket = mongoDriver.getLatestTicket(id);
        } catch (Throwable throwable) {
            return Observable.just(EXCEPTION_MESSAGE + throwable.getMessage());
        }

        if (ticket == null) {
            return Observable.just(NO_TICKETS_MESSAGE);
        }

        Date endDate = ticket.getExpirationDate();
        if (date.after(endDate)) {
            return Observable.just(TICKET_EXPIRED_MESSAGE);
        }

        Event event = new Event(id, date, Event.EventType.ENTER);
        if (mongoDriver.addEvent(event) == Success.SUCCESS) {
            return Observable.just(NEW_ENTER_EVENT_MESSAGE);
        } else {
            return Observable.just(ERROR_MESSAGE);
        }
    }

    public Observable<String> addExit(Map<String, List<String>> params, Date date) {
        String validation = validate(params);
        if (validation.length() > 0) {
            return Observable.just(validation);
        }

        int id = Integer.parseInt(params.get(TICKET_ID_FIELD).get(0));
        Event event = new Event(id, date, Event.EventType.EXIT);

        if (mongoDriver.addEvent(event) == Success.SUCCESS) {
            return Observable.just(NEW_EXIT_EVENT_MESSAGE);
        } else {
            return Observable.just(ERROR_MESSAGE);
        }
    }

    private String validate(Map<String, List<String>> params) {
        if (params.containsKey(TICKET_ID_FIELD)) {
            return "";
        }
        return NO_TICKET_ID_MESSAGE;
    }
}
