package model;

import org.bson.Document;

public class User {

    private final int id;
    private final String name;
    private final Currency currency;

    private final static String ID_FIELD = "id";
    private final static String NAME_FIELD = "name";
    private final static String CURRENCY_FIELD = "currency";

    public User(int id, String name, Currency currency) {
        this.id = id;
        this.name = name;
        this.currency = currency;
    }

    public User(Document doc) {
        this(
                doc.getInteger(ID_FIELD),
                doc.getString(NAME_FIELD),
                Enum.valueOf(Currency.class, doc.getString(CURRENCY_FIELD).toUpperCase())
        );
    }

    public Currency getCurrency() {
        return currency;
    }

    public Document toDocument() {
        return new Document("id", id)
                .append("name", name)
                .append("currency", currency.toString());
    }

    @Override
    public String toString() {
        return "User: {\n" +
                "\tid: " + id + ",\n" +
                "\tname: " + name + ",\n" +
                "\tcurrency: " + currency.toString() + "\n" +
                "}";
    }

    public enum Currency {
        RUB, EUR, USD
    }
}
