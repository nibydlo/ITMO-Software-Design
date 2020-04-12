package server;

import com.mongodb.rx.client.Success;
import dao.MongoDriver;
import io.reactivex.netty.protocol.http.server.HttpServer;
import model.Ticket;
import rx.Observable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class ManagerServer {

    private final static int PORT = 8081;
    private final static String DATE_PATTERN = "dd-MM-yyyy";

    private final static String TICKET_ID_FIELD = "ticket-id";
    private final static String DATE_FIELD = "date";

    private final static String GET_TICKET_INFO_METHOD = "getTicketInfo";
    private final static String ADD_TICKET_INFO_METHOD = "addTicketInfo";

    private final static String NO_TICKETS_MESSAGE = "No tickets found";
    private final static String UNKNOWN_COMMAND_MESSAGE = "Unknown command";
    private final static String EXCEPTION_MESSAGE = "Exception occured with message: ";
    private final static String INCORRECT_DATE_FORMAT_MESSAGE = "Incorrect date format";
    private final static String MODIFIED_TICKET_MESSAGE = "Created or updated ticket";
    private final static String CANT_ADD_TICKET_MESSAGE = "Exception updating expiration date";
    private final static String NOT_ENOUGH_PARAMS_MESSAGE = "Please, add params: ";

    private final static List<String> GET_TICKET_PARAMS = Collections.singletonList(TICKET_ID_FIELD);
    private final static List<String> UPDATE_TICKET_PARAMS = Arrays.asList(TICKET_ID_FIELD, DATE_FIELD);

    private final MongoDriver mongoDriver;

    public ManagerServer(MongoDriver mongoDriver) {
        this.mongoDriver = mongoDriver;
    }

    public void run() {
        HttpServer.newServer(PORT).start((request, response) -> {
            String command = request.getDecodedPath().substring(1);
            Map<String, List<String>> params = request.getQueryParameters();
            if (GET_TICKET_INFO_METHOD.equals(command)) {
                return response.writeString(getTicket(params));
            }
            if (ADD_TICKET_INFO_METHOD.equals(command)) {
                return response.writeString(handleTicketOperation(params, new Date()));
            }
            return response.writeString(Observable.just(UNKNOWN_COMMAND_MESSAGE));
        }).awaitShutdown();
    }

    public Observable<String> getTicket(Map<String, List<String>> params) {
        String validation = validate(params, GET_TICKET_PARAMS);
        if (validation.length() > 0) {
            return Observable.just(validation);
        }

        int id = Integer.parseInt(params.get(TICKET_ID_FIELD).get(0));
        try {
            Ticket ticket = mongoDriver.getLatestTicket(id);
            if (ticket == null) {
                return Observable.just(NO_TICKETS_MESSAGE);
            } else {
                return Observable.just(ticket.toString());
            }
        } catch (Throwable throwable) {
            return Observable.just(EXCEPTION_MESSAGE + throwable.getMessage());
        }
    }

    public Observable<String> handleTicketOperation(Map<String, List<String>> params, Date creationDate) {
        String validation = validate(params, UPDATE_TICKET_PARAMS);
        if (validation.length() > 0) {
            return Observable.just(validation);
        }

        int id = Integer.parseInt(params.get(TICKET_ID_FIELD).get(0));
        Date date;
        try {
            date = new SimpleDateFormat(DATE_PATTERN).parse(params.get(DATE_FIELD).get(0));
        } catch (ParseException e) {
            return Observable.just(INCORRECT_DATE_FORMAT_MESSAGE);
        }
        return addTicket(id, date, creationDate);
    }

    public Observable<String> addTicket(int id, Date expirationDate, Date startDate) {
        if (mongoDriver.addTicket(new Ticket(id, startDate, expirationDate)) == Success.SUCCESS) {
            return Observable.just(MODIFIED_TICKET_MESSAGE);
        } else {
            return Observable.just(CANT_ADD_TICKET_MESSAGE);
        }
    }

    private String validate(Map<String, List<String>> params, List<String> expectedParams) {
        List<String> missingParams = expectedParams.stream().filter(param -> !params.containsKey(param)).collect(Collectors.toList());
        if (missingParams.isEmpty()) {
            return "";
        }
        return NOT_ENOUGH_PARAMS_MESSAGE + String.join(", ", missingParams);
    }
}
