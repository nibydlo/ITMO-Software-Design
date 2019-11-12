package com.krylov.software_design.lab_2;

import com.google.common.collect.ImmutableList;
import com.krylov.software_design.lab_2.domain.HashTagStat;
import com.krylov.software_design.lab_2.domain.NewsFeed;
import com.krylov.software_design.lab_2.service.SearchService;
import com.krylov.software_design.lab_2.client.VkClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.Instant;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class SearchServiceTest {
    private final static String HASHTAG = "lol";
    private final static int NORMAL_HOUR = 7;
    private final static int COUNT_1 = 1;
    private final static long ID = 100000001;
    private final static int HOUR_6 = 6;
    private final static Instant TIME_6 = Instant.ofEpochSecond(3600*6);
    private final static int HOUR_3 = 3;
    private final static Instant TIME_3 = Instant.ofEpochSecond(3600*3);

    @Mock
    private VkClient vkClient;

    @InjectMocks
    private SearchService searchService;

    @Test
    public void regularTest() {
        Mockito.when(vkClient.getNewsFeedsByHashTag(HASHTAG, NORMAL_HOUR)).thenReturn(ImmutableList.of(
                new NewsFeed(ID, TIME_6),
                new NewsFeed(ID, TIME_3)
        ));

        List<HashTagStat> res = ImmutableList.of(
                new HashTagStat(HASHTAG, COUNT_1, HOUR_6),
                new HashTagStat(HASHTAG, COUNT_1, HOUR_3)
        );

        assert res.equals(searchService.countNewsFeedsWithHashTag(HASHTAG, NORMAL_HOUR));
    }
}
