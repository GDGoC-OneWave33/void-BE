package com.example.demo.domain.ranking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RankingService {

    private final StringRedisTemplate redisTemplate;
    // 20분마다 갱신된 결과를 저장할 메모리 캐시
    private List<Map<String, Object>> cachedTop3Ranking = new ArrayList<>();

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
        if (keyword.contains("*")) return;
        String key = getRankingkey();
        redisTemplate.opsForZSet().incrementScore(key, keyword, 1);
    }

    // 20분마다 무거운 계산(전체 합계 및 퍼센트)을 미리 수행
    @Scheduled(fixedRate = 1200000) // 20분(ms 단위)
    public void updateRankingCache() {
        System.out.println("### [스케줄러] 20분 주기 랭킹 캐시 갱신 시작");
        this.cachedTop3Ranking = calculateTop3WithPercentage();
    }

    public List<Map<String, Object>> getTop3() {
        if (cachedTop3Ranking.isEmpty()) {
            return calculateTop3WithPercentage(); // 캐시가 비어있을 때만 직접 계산
        }
        return cachedTop3Ranking;
    }

    private List<Map<String, Object>> calculateTop3WithPercentage() {
        String key = getRankingkey();
        Set<ZSetOperations.TypedTuple<String>> top3WithScores =
                redisTemplate.opsForZSet().reverseRangeWithScores(key, 0, 2);

        if (top3WithScores == null || top3WithScores.isEmpty()) return List.of();

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
            double percentage = finalTotalScore > 0 ? (score / finalTotalScore) * 100 : 0;

            map.put("keyword", tuple.getValue());
            map.put("percentage", Math.round(percentage) + "%");
            return map;
        }).collect(Collectors.toList());
    }
    }

