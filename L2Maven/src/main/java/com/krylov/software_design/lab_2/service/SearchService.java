package com.krylov.software_design.lab_2.service;

import com.krylov.software_design.lab_2.client.VkClient;
import com.krylov.software_design.lab_2.domain.HashTagStat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class SearchService {

    private VkClient vkClient;

    @Autowired
    public SearchService(VkClient vkClient) {
        this.vkClient = vkClient;
    }

    public List<HashTagStat> countNewsFeedsWithHashTag(String hashTag, int hours) {
        return vkClient.getNewsFeedsByHashTag(hashTag, hours)
                .stream()
                .map(item -> item.getInstant().atZone(ZoneOffset.UTC).getHour())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet()
                .stream()
                .map(entry -> new HashTagStat(hashTag, entry.getValue(), entry.getKey()))
                .sorted(Comparator.comparing(HashTagStat::getHour).reversed())
                .collect(Collectors.toList());
    }
}
