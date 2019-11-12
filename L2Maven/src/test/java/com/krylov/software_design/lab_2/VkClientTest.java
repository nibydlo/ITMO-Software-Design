package com.krylov.software_design.lab_2;

import com.krylov.software_design.lab_2.domain.NewsFeed;
import com.krylov.software_design.lab_2.client.VkClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@RunWith(SpringRunner.class)
@SpringBootTest
public class VkClientTest {

    private static final String HASH_TAG_TXT = "лол";
    private static final int HOUR_24 = 24;
    private static String responseData;

    @Autowired
    private VkClient vkClient;
    @Autowired
    private RestTemplate restTemplate;
    private MockRestServiceServer mockService;

    // for building url
    private final static String vkApiVersion = "5.21";
    private final static String uid = "7184876";
    private final static String token = "acfacf5facfacf5facfacf5f3bac976eb3aacfaacfacf5ff14e8a0f8bb86d6c877709fd";
    private final static String vkApiUrl = "https://api.vk.com/method/";
    private final static String VK_SEARCH_URL = "newsfeed.search";
    private final static int SECONDS_IN_HOUR = 3600;

    private final static String PATH_TO_FILLED_RESPONSE = "/response.json";
    private final static String PATH_TO_EMPTY_RESPONSE = "/empty_response.json";
    @Before
    public void setup() {
        this.mockService = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    public void findNonEmptyTest() throws URISyntaxException, IOException {
        Path path = Paths.get(VkClient.class.getResource(PATH_TO_FILLED_RESPONSE).toURI());
        responseData = Files.lines(path).collect(Collectors.joining());
        String url = buildUrl(HASH_TAG_TXT, HOUR_24);

        mockService.expect(requestTo(url))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(responseData)
                );

        List<NewsFeed> newsFeeds = vkClient.getNewsFeedsByHashTag(HASH_TAG_TXT, HOUR_24);
        mockService.verify();

        assertThat(newsFeeds).hasSize(7);
        assertThat(newsFeeds).contains(
                new NewsFeed(15448, Instant.ofEpochSecond(1572111286)),
                new NewsFeed(7810, Instant.ofEpochSecond(1572111252)),
                new NewsFeed(9792, Instant.ofEpochSecond(1572111214)),
                new NewsFeed(15447, Instant.ofEpochSecond(1572111170)),
                new NewsFeed(31199, Instant.ofEpochSecond(1572111120)),
                new NewsFeed(1880, Instant.ofEpochSecond(1572111106)),
                new NewsFeed(514, Instant.ofEpochSecond(1572110883))
        );
    }

    @Test
    public void findEmpty() throws IOException, URISyntaxException {
        Path path = Paths.get(VkClient.class.getResource(PATH_TO_EMPTY_RESPONSE).toURI());
        responseData = Files.lines(path).collect(Collectors.joining());
        String url = buildUrl(HASH_TAG_TXT, HOUR_24);

        mockService.expect(requestTo(url))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(responseData)
                );

        List<NewsFeed> newsFeeds = vkClient.getNewsFeedsByHashTag(HASH_TAG_TXT, HOUR_24);
        assertThat(newsFeeds).isEmpty();
    }

    private String buildUrl(String hashTag, int hours) {
        return UriComponentsBuilder.fromHttpUrl(vkApiUrl)
                .path(VK_SEARCH_URL)
                .queryParam("v", vkApiVersion)
                .queryParam("uid", uid)
                .queryParam("access_token", token)
                .queryParam("q", '#' + hashTag)
                .queryParam("start_time", convertTimeIntervalToUnixTime(hours))
                .toUriString();
    }

    private static int convertTimeIntervalToUnixTime(int hours) {
        return (int) Instant.now()
                .minusSeconds(hours * SECONDS_IN_HOUR)
                .getEpochSecond();
    }
}
