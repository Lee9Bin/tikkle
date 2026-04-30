package com.taesan.tikkle.domain.rank.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taesan.tikkle.domain.rank.dto.response.RankEntryResponse;
import com.taesan.tikkle.domain.rank.dto.response.RankResponse;
import com.taesan.tikkle.domain.rank.entity.RankSnapshot;
import com.taesan.tikkle.domain.rank.entity.RankSnapshotStatus;
import com.taesan.tikkle.domain.rank.repository.RankSnapshotEntryRepository;
import com.taesan.tikkle.domain.rank.repository.RankSnapshotRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RankService {

	private final RankSnapshotRepository rankSnapshotRepository;
	private final RankSnapshotEntryRepository rankSnapshotEntryRepository;

	@Transactional(readOnly = true)
	public RankResponse getRanks(UUID username, String keyword, Pageable pageable) {
		Optional<RankSnapshot> optionalRankSnapshot = findLatestCompletedRankSnapshot();

		if (optionalRankSnapshot.isEmpty()) {
			return RankResponse.of(Page.empty(pageable), null);
		}

		RankSnapshot rankSnapshot = optionalRankSnapshot.get();
		Page<RankEntryResponse> rankList = findRankList(rankSnapshot, keyword, pageable);

		RankEntryResponse myRank = rankSnapshotEntryRepository.findByRankSnapshotAndMemberId(rankSnapshot, username)
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
