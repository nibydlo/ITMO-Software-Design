package com.krylov.software_design.lab_2;

import com.krylov.software_design.lab_2.controller.SearchController;
import com.krylov.software_design.lab_2.domain.HashTagStat;
import com.krylov.software_design.lab_2.service.SearchService;
import org.junit.jupiter.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import com.google.common.collect.ImmutableList;

import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class ControllerTest {
    private final static String HASHTAG = "lol";
    private final static String INVALID_HASHTAG = "%*%";
    private final static int ZERO_HOUR = 0;
    private final static int NORMAL_HOUR = 7;
    private final static int TOO_BIG_HOUR = 25;
    private final static int SAMPLE_COUNT = 5;
    private final static String INVALID_HOUR_MESSAGE = "Hours param should be between 1 and 24";
    private final static String INVALID_HASHTAG_MESSAGE = "Invalid hashtag";
    @Mock
    private SearchService searchService;

    @InjectMocks
    private SearchController searchController;

    @Test
    public void zeroHourTest() {
        IllegalArgumentException e = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> searchController.getStats(HASHTAG, ZERO_HOUR)
        );
        assert e.getMessage().equals(INVALID_HOUR_MESSAGE);
    }

    @Test
    public void tooBigHourTest() {
        IllegalArgumentException e = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> searchController.getStats(HASHTAG, TOO_BIG_HOUR)
        );
        assert e.getMessage().equals(INVALID_HOUR_MESSAGE);
    }

    @Test
    public void incorrectHashTagTest() {
        IllegalArgumentException e = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> searchController.getStats(INVALID_HASHTAG, NORMAL_HOUR)
        );
        assert e.getMessage().equals(INVALID_HASHTAG_MESSAGE);
    }

    @Test
    public void correctTest() {
        List<HashTagStat> res = ImmutableList.of(new HashTagStat(HASHTAG, SAMPLE_COUNT, NORMAL_HOUR));
        Mockito.when(searchService.countNewsFeedsWithHashTag(HASHTAG, NORMAL_HOUR)).thenReturn(res);
        assert res.equals(searchController.getStats(HASHTAG, NORMAL_HOUR));
    }
}
