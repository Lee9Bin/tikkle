package com.taesan.tikkle.domain.rank.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taesan.tikkle.domain.rank.dto.response.RankEntryResponse;
import com.taesan.tikkle.domain.rank.dto.response.RankResponse;
import com.taesan.tikkle.domain.rank.dto.response.TopRankResponse;
import com.taesan.tikkle.domain.rank.entity.RankSnapshot;
import com.taesan.tikkle.domain.rank.entity.RankSnapshotStatus;
import com.taesan.tikkle.domain.rank.repository.RankSnapshotEntryRepository;
import com.taesan.tikkle.domain.rank.repository.RankSnapshotRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RankService {

	private static final int TOP_RANK_LIMIT = 10;

	private final RankCacheService rankCacheService;
	private final RankSnapshotRepository rankSnapshotRepository;
	private final RankSnapshotEntryRepository rankSnapshotEntryRepository;

	@Transactional(readOnly = true)
	public List<TopRankResponse> getTopRanks() {
		return rankCacheService.getTopRanks().orElseGet(this::findTopRanksAndCache);
	}

	@Transactional(readOnly = true)
	public RankResponse getRanks(UUID memberId, String keyword, Pageable pageable) {
		return findLatestCompletedRankSnapshot()
			.map(rankSnapshot -> getRankResponse(rankSnapshot, memberId, keyword, pageable))
			.orElseGet(() -> RankResponse.empty(pageable));
	}

	@Transactional(readOnly = true)
	public void refreshTopRanksCache() {
		rankCacheService.cacheTopRanks(findTopRanks());
	}

	private List<TopRankResponse> findTopRanksAndCache() {
		List<TopRankResponse> topRanks = findTopRanks();
		rankCacheService.cacheTopRanks(topRanks);
		return topRanks;
	}

	private List<TopRankResponse> findTopRanks() {
		return findLatestCompletedRankSnapshot()
			.map(rankSnapshot -> rankSnapshotEntryRepository
				.findAllByRankSnapshotOrderByPositionAsc(rankSnapshot, PageRequest.of(0, TOP_RANK_LIMIT))
				.stream()
				.map(TopRankResponse::from)
				.toList())
			.orElseGet(List::of);
	}

	private RankResponse getRankResponse(RankSnapshot rankSnapshot, UUID memberId, String keyword, Pageable pageable) {
		Page<RankEntryResponse> rankList = findRankList(rankSnapshot, keyword, pageable);

		RankEntryResponse myRank = rankSnapshotEntryRepository.findByRankSnapshotAndMemberId(rankSnapshot, memberId)
			.map(RankEntryResponse::from)
			.orElse(null);

		return RankResponse.of(rankList, myRank);
	}

	private Optional<RankSnapshot> findLatestCompletedRankSnapshot() {
		return rankSnapshotRepository.findTopByStatusOrderByCreatedAtDesc(RankSnapshotStatus.COMPLETED);
	}

	private Page<RankEntryResponse> findRankList(RankSnapshot rankSnapshot, String keyword, Pageable pageable) {
		if (keyword == null || keyword.isBlank()) {
			return rankSnapshotEntryRepository.findByRankSnapshotOrderByPositionAsc(rankSnapshot, pageable)
				.map(RankEntryResponse::from);
		}

		return rankSnapshotEntryRepository
			.findByRankSnapshotAndMemberNameContainingOrderByPositionAsc(rankSnapshot, keyword, pageable)
			.map(RankEntryResponse::from);
	}
}
