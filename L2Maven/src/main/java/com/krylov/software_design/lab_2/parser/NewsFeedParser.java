package com.krylov.software_design.lab_2.parser;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.krylov.software_design.lab_2.domain.NewsFeed;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
public class NewsFeedParser {
    public List<NewsFeed> parseJsonToNewsFeeds(String json) {
        JsonObject jsonObject = new Gson().fromJson(json, JsonObject.class);
        if (!jsonObject.has("response")) {
            throw new JsonParseException("no response field");
        }
        if (!jsonObject.getAsJsonObject("response").has("items")) {
            throw new JsonParseException("no items field");
        }

        try {
            JsonArray jsonArray = jsonObject
                    .getAsJsonObject("response")
                    .getAsJsonArray("items");

            List<NewsFeed> newsFeeds = new ArrayList<>();

            jsonArray.forEach(jsonElement -> {
                JsonObject currObject = jsonElement.getAsJsonObject();
                int id;
                try {
                    id = currObject.get("id").getAsInt();
                } catch (Exception e) {
                    throw new JsonParseException("id is absent or incorrect");
                }
                Instant date;
                try {
                    date = Instant.ofEpochSecond(currObject.get("date").getAsInt());
                } catch (Exception e) {
                    throw new JsonParseException("date is absent or incorrect");
                }

                NewsFeed newsFeed = new NewsFeed(id, date);
                newsFeeds.add(newsFeed);
            });

            return newsFeeds;
        } catch (ClassCastException e) {
            throw new ClassCastException("items is not an array");
        }
    }
}
