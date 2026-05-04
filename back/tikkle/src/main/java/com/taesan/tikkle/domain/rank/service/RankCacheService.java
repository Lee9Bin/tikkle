package com.taesan.tikkle.domain.rank.service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taesan.tikkle.domain.rank.dto.response.TopRankResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RankCacheService {

	private static final String TOP_RANKS_KEY = "rank:top10:latest";
	private static final Duration TOP_RANKS_TTL = Duration.ofMinutes(65);
	private static final TypeReference<List<TopRankResponse>> TOP_RANKS_TYPE = new TypeReference<>() {
	};

	private final RedisTemplate<String, String> redisTemplate;
	private final ObjectMapper objectMapper;

	public Optional<List<TopRankResponse>> getTopRanks() {
		try {
			String cachedTopRanks = redisTemplate.opsForValue().get(TOP_RANKS_KEY);

			if (cachedTopRanks == null) {
				return Optional.empty();
			}

			return Optional.of(objectMapper.readValue(cachedTopRanks, TOP_RANKS_TYPE));
		} catch (JsonProcessingException | DataAccessException exception) {
			log.warn("상위 랭킹 캐시 조회에 실패했습니다.", exception);
			return Optional.empty();
		}
	}

	public void cacheTopRanks(List<TopRankResponse> topRanks) {
		if (topRanks.isEmpty()) {
			return;
		}

		try {
			String cacheValue = objectMapper.writeValueAsString(topRanks);
			redisTemplate.opsForValue().set(TOP_RANKS_KEY, cacheValue, TOP_RANKS_TTL);
		} catch (JsonProcessingException | DataAccessException exception) {
			log.warn("상위 랭킹 캐시 저장에 실패했습니다.", exception);
		}
	}
}
