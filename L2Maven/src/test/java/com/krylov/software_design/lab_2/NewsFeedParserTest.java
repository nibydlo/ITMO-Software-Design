package com.krylov.software_design.lab_2;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonParseException;
import com.krylov.software_design.lab_2.domain.NewsFeed;
import com.krylov.software_design.lab_2.parser.NewsFeedParser;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.time.Instant;

public class NewsFeedParserTest {
    private final static int ID = 123;
    private final static long DATE = 12;
    private final static String NO_RESPONSE_MESSAGE = "no response field";
    private final static String NO_ITEMS_MESSAGE = "no items field";
    private final static String ITEMS_NOT_ARRAY_MESSAGE = "items is not an array";
    private final static String ID_MESSAGE = "id is absent or incorrect";
    private final static String DATE_MESSAGE = "date is absent or incorrect";
    private final static String EMPTY_JSON = "{}";
    private final static String JSON_WITHOUT_ITEMS = "{response:{}}";
    private final static String JSON_WITH_ITEMS_NOT_ARRAY = "{response:{items:{}}}";
    private final static String JSON_WITHOUT_ID = "{response:{items:[{date:" + DATE + "}]}}";
    private final static String JSON_WITHOUT_DATE = "{response:{items:[{id:" + ID + "}]}}";
    private final static String CORRECT_JSON = "{response:{items:[{id:" + ID + ", date:" + DATE + "}]}}";

    private final NewsFeedParser parser = new NewsFeedParser();

    @Test
    public void emptyJsonTest() {
        JsonParseException e = Assertions.assertThrows(
                JsonParseException.class,
                () -> parser.parseJsonToNewsFeeds(EMPTY_JSON)
        );
        assert e.getMessage().equals(NO_RESPONSE_MESSAGE);
    }

    @Test
    public void jsonWithoutItemsTest() {
        JsonParseException e = Assertions.assertThrows(
                JsonParseException.class,
                () -> parser.parseJsonToNewsFeeds(JSON_WITHOUT_ITEMS)
        );
        assert e.getMessage().equals(NO_ITEMS_MESSAGE);
    }

    @Test
    public void itemsNotArrayTest() {
        ClassCastException e = Assertions.assertThrows(
                ClassCastException.class,
                () -> parser.parseJsonToNewsFeeds(JSON_WITH_ITEMS_NOT_ARRAY)
        );
        assert e.getMessage().equals(ITEMS_NOT_ARRAY_MESSAGE);
    }

    @Test
    public void jsonWithoutId() {
        JsonParseException e = Assertions.assertThrows(
                JsonParseException.class,
                () -> parser.parseJsonToNewsFeeds(JSON_WITHOUT_ID)
        );
        assert e.getMessage().equals(ID_MESSAGE);
    }

    @Test
    public void jsonWithoutDate() {
        JsonParseException e = Assertions.assertThrows(
                JsonParseException.class,
                () -> parser.parseJsonToNewsFeeds(JSON_WITHOUT_DATE)
        );
        assert e.getMessage().equals(DATE_MESSAGE);
    }

    @Test
    public void okTest() {
        NewsFeed correct = new NewsFeed(ID, Instant.ofEpochSecond(DATE));
        assert parser.parseJsonToNewsFeeds(CORRECT_JSON).equals(ImmutableList.of(correct));
    }
}
