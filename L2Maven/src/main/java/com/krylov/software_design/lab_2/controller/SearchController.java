package com.krylov.software_design.lab_2.controller;

import com.krylov.software_design.lab_2.domain.HashTagStat;
import com.krylov.software_design.lab_2.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SearchController {
    private final SearchService searchService;

    private static final int MIN_HOUR = 1;
    private static final int MAX_HOUR = 24;
    private static final String HASH_TAG_REGEX = "(?:\\s|^)#[А-Яа-яA-Za-z0-9\\-\\.\\_]+(?:\\s|$)";

    @Autowired
    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping(value = "/search")
    public List<HashTagStat> getStats(
            @RequestParam("hashtag") String hashTag,
            @RequestParam("hours") int hours
    ) {
        validate(hashTag, hours);
        return searchService.countNewsFeedsWithHashTag(hashTag, hours);
    }

    private static void validate(String hashTag, Integer hours) {
        validateHashTag(hashTag);
        validateHours(hours);
    }

    private static void validateHours(int hours) {
        if (hours < MIN_HOUR || hours > MAX_HOUR) {
            throw new IllegalArgumentException("Hours param should be between 1 and 24");
        }
    }

    private static void validateHashTag(String hashTag) {
        if (!("#" + hashTag).matches(HASH_TAG_REGEX)) {
            throw new IllegalArgumentException("Invalid hashtag");
        }
    }
}