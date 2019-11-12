package com.krylov.software_design.lab_2.client;

import com.krylov.software_design.lab_2.domain.NewsFeed;
import com.krylov.software_design.lab_2.parser.NewsFeedParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Instant;
import java.util.Collections;
import java.util.List;


@Component
public class VkClient {
    private NewsFeedParser newsFeedParser;

    @Value("${vk.api.version}")
    private String vkApiVersion;

    @Value("${vk.app.id}")
    private String uid;

    @Value("${vk.service.token}")
    private String token;

    @Value("${vk.api.url}")
    private String vkApiUrl;

    private final static String VK_SEARCH_URL = "newsfeed.search";
    private final static int SECONDS_IN_HOUR = 3600;
    private final RestTemplate restTemplate;

    @Autowired
    public VkClient(NewsFeedParser newsFeedParser, RestTemplate restTemplate) {
        this.newsFeedParser = newsFeedParser;
        this.restTemplate = restTemplate;
    }

    public List<NewsFeed> getNewsFeedsByHashTag(String hashTag, int hours) {
        String url = buildUrl(hashTag, hours);

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            if (!response.getStatusCode().equals(HttpStatus.OK)) {
                throw new RuntimeException("Response with status: " + response.getStatusCode());
            } else if (response.getBody() != null) {
                return newsFeedParser.parseJsonToNewsFeeds(response.getBody());
            }
        } catch (RestClientException ex) {
            throw new RuntimeException("Can't get information for hashTag='" + hashTag + "'");
        }
        return Collections.emptyList();
    }

    private String buildUrl(String hashTagTxt, int hours) {

        return UriComponentsBuilder.fromHttpUrl(vkApiUrl)
                .path(VK_SEARCH_URL)
                .queryParam("v", vkApiVersion)
                .queryParam("uid", uid)
                .queryParam("access_token", token)
                .queryParam("q", '#' + hashTagTxt)
                .queryParam("start_time", convertTimeIntervalToUnixTime(hours))
                .toUriString();
    }

    private static int convertTimeIntervalToUnixTime(int hours) {
        return (int) Instant.now()
                .minusSeconds(hours * SECONDS_IN_HOUR)
                .getEpochSecond();
    }
}
