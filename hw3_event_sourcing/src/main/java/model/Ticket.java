package model;

import org.bson.Document;

import java.util.Date;

public class Ticket {

    private final static String ID_FIELD = "id";
    private final static String EXPIRATION_DATE_FIELD = "expirationDate";
    private final static String START_DATE_FIELD = "startDate";

    private final Integer id;
    private final Date startDate;
    private final Date expirationDate;

    public Ticket(int id, Date startDate, Date expirationDate) {
        this.id = id;
        this.startDate = startDate;
        this.expirationDate = expirationDate;
    }

    public Ticket(Document document) {
        this(
                document.getInteger(ID_FIELD),
                document.getDate(START_DATE_FIELD),
                document.getDate(EXPIRATION_DATE_FIELD)
        );
    }

    public int getId() {
        return id;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public Document toDocument() {
        return new Document(ID_FIELD, id)
                .append(START_DATE_FIELD, startDate)
                .append(EXPIRATION_DATE_FIELD, expirationDate);
    }

    @Override
    public String toString() {
        return String.format(
                "Ticket: id = %s, start date = %s, expiration date = %s",
                id.toString(),
                startDate.toString(),
                expirationDate.toString()
        );
    }
}
