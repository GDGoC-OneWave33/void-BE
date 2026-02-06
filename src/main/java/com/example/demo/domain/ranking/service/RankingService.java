package com.example.demo.domain.ranking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RankingService {

    private final StringRedisTemplate redisTemplate;

    // 시간별 redis key 생성
    private String getRankingkey() {
        String timestamp = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyyMMddHH")
        );

        return "ranking:" + timestamp;
    }

    // 키워드 소각 횟수 증가
    public void incrementKeywordCount(String keyword) {
        if (keyword == null || keyword.isBlank()) return;
        String key = getRankingkey();
        redisTemplate.opsForZSet().incrementScore(key, keyword, 1);
    }

    public List<Map<String, Object>> getTop3WithPercentage() {
        String key = getRankingkey();

        Set<ZSetOperations.TypedTuple<String>> top3WithScores =
                redisTemplate.opsForZSet().reverseRangeWithScores(key, 0, 2);

        if (top3WithScores == null || top3WithScores.isEmpty()) return List.of();

        // 현재 시간대의 전체 소각 횟수 합
        Double totalScore = 0.0;
        Set<ZSetOperations.TypedTuple<String>> allItems =
                redisTemplate.opsForZSet().rangeWithScores(key, 0, -1);

        if (allItems != null) {
            for (var item : allItems) {
                totalScore += (item.getScore() != null ? item.getScore() : 0.0);
            }
        }
        final Double finalTotalScore = totalScore;

        return top3WithScores.stream().map(tuple -> {
            Map<String, Object> map = new LinkedHashMap<>();
            double score = tuple.getScore() != null ? tuple.getScore() : 0.0;
            double percentage = (score / finalTotalScore) * 100;

            map.put("keyword", tuple.getValue());
            map.put("percentage", Math.round(percentage) + "%");
            return map;
        }).collect(Collectors.toList());
    }


}
