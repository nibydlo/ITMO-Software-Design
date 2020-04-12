package server;

import dao.MongoDriver;
import io.reactivex.netty.protocol.http.server.HttpServer;
import model.Event;
import rx.Observable;
import services.EventNotificationService;

import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReportServer {

    private final static Integer MS = 1000;
    private final static Integer SEC = 60;
    private final static Integer MIN = 60;
    private final static Integer PORT = 8082;
    private final static String STATS_METHOD = "stats";
    private final static String AVERAGE_LENGTH_METHOD = "average-length";
    private final static String UNKNOWN_COMMAND_MESSAGE = "Unknown command";
    private final static String NO_SESSION_MESSAGE = "No sessions found";
    private final static String AVERAGE_TIME_MESSAGE = "Average training length is %s hours, %s minutes";
    private final EventNotificationService notificationService;
    private List<Event> events;


    public ReportServer(MongoDriver mongoDriver, EventNotificationService notificationService) throws Throwable {
        this.notificationService = notificationService;
        events = mongoDriver.getEvents();
    }

    public void run() {
        HttpServer.newServer(PORT).start((request, response) -> {
            String command = request.getDecodedPath().substring(1);

            if (STATS_METHOD.equals(command)) {
                return response.writeString(Observable.just(getStats()));
            }

            if (AVERAGE_LENGTH_METHOD.equals(command)) {
                return response.writeString(Observable.just(getAverageLength()));
            }

            return response.writeString(Observable.just(UNKNOWN_COMMAND_MESSAGE));
        }).awaitShutdown();
    }

    private void updateEvents() {
        while (notificationService.getSize() > 0) {
            Event event = notificationService.peek();
            events.add(event);
            notificationService.pop();
        }
    }

    public String getStats() {
        updateEvents();
        Map<String, List<Event>> eventsByDay = events
                .stream()
                .filter(e -> e.getEventType() == Event.EventType.ENTER)
                .collect(Collectors.groupingBy(event -> {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(event.getTime());
                    return calendar.get(Calendar.DAY_OF_MONTH)
                            + " " + (calendar.get(Calendar.MONTH) + 1)
                            + " " + calendar.get(Calendar.YEAR);
                }));

        StringBuilder stringBuilder = new StringBuilder();
        eventsByDay
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .forEachOrdered(
                        entry -> stringBuilder
                                .append(entry.getKey())
                                .append(": ")
                                .append(entry
                                        .getValue()
                                        .stream()
                                        .distinct()
                                        .count())
                                .append("\n")
                );

        return stringBuilder.toString();
    }

    public String getAverageLength() {
        updateEvents();
        Map<Integer, List<Event>> eventsByTicketId = events
                .stream()
                .collect(Collectors.groupingBy(Event::getTicketId));

        long minutesSum = 0;
        int recordedSessions = 0;

        Comparator<Event> eventComparator = Comparator.comparingLong(event -> event.getTime().getTime());

        for (List<Event> eventList : eventsByTicketId.values()) {
            eventList.sort(eventComparator);
            Event previous = null;
            for (Event event : eventList) {
                if (event.getEventType() == Event.EventType.ENTER) {
                    previous = event;
                } else if (previous != null && event.getEventType() == Event.EventType.EXIT) {
                    minutesSum += (event.getTime().getTime() - previous.getTime().getTime()) / MS / SEC;
                    recordedSessions++;
                    previous = null;
                }
            }
        }

        if (recordedSessions == 0) {
            return NO_SESSION_MESSAGE;
        }

        int averageMinutes = (int) (minutesSum / recordedSessions);

        return String.format(AVERAGE_TIME_MESSAGE,
                averageMinutes / MIN,
                averageMinutes % MIN);
    }
}
