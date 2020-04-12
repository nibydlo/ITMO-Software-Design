package model;

import org.bson.Document;

import java.util.Date;

public class Event {

    private final static String TICKET_ID_FIELD = "ticketId";
    private final static String TIME_FIELD = "time";
    private final static String EVENT_TYPE_FIELD = "eventType";

    private final int ticketId;
    private final Date time;
    private final EventType eventType;

    public Event(int ticketId, Date time, EventType eventType) {
        this.ticketId = ticketId;
        this.time = time;
        this.eventType = eventType;
    }

    public Event(Document document) {
        this(
                document.getInteger(TICKET_ID_FIELD),
                document.getDate(TIME_FIELD),
                Enum.valueOf(EventType.class, document.getString(EVENT_TYPE_FIELD).toUpperCase())
        );
    }

    public int getTicketId() {
        return ticketId;
    }

    public Date getTime() {
        return time;
    }

    public EventType getEventType() {
        return eventType;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Event other = (Event) obj;
        return this.ticketId == other.ticketId
                && this.time.equals(other.time)
                && this.eventType == other.eventType;
    }

    public Document toDocument() {
        return new Document(TICKET_ID_FIELD, ticketId)
                .append(TIME_FIELD, time)
                .append(TIME_FIELD, eventType.toString());
    }

    public enum EventType {
        ENTER, EXIT
    }
}
