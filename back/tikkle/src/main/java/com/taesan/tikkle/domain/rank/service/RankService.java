package com.taesan.tikkle.domain.rank.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taesan.tikkle.domain.rank.dto.response.RankEntryResponse;
import com.taesan.tikkle.domain.rank.dto.response.RankResponse;
import com.taesan.tikkle.domain.rank.entity.RankSnapshot;
import com.taesan.tikkle.domain.rank.entity.RankSnapshotStatus;
import com.taesan.tikkle.domain.rank.repository.RankSnapshotEntryRepository;
import com.taesan.tikkle.domain.rank.repository.RankSnapshotRepository;
import com.taesan.tikkle.global.errors.ErrorCode;
import com.taesan.tikkle.global.exceptions.CustomException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RankService {

	private final RankSnapshotRepository rankSnapshotRepository;
	private final RankSnapshotEntryRepository rankSnapshotEntryRepository;

	@Transactional(readOnly = true)
	public RankResponse getRanks(UUID username, String keyword, Pageable pageable) {
		RankSnapshot rankSnapshot = findLatestCompletedRankSnapshot();
		List<RankEntryResponse> rankList = findRankList(rankSnapshot, keyword, pageable);

		RankEntryResponse myRank = rankSnapshotEntryRepository.findByRankSnapshotAndMemberId(rankSnapshot, username)
			.map(RankEntryResponse::from)
			.orElse(null);

		return RankResponse.of(rankList, myRank);
	}

	private RankSnapshot findLatestCompletedRankSnapshot() {
		return rankSnapshotRepository.findTopByStatusOrderByCreatedAtDesc(RankSnapshotStatus.COMPLETED)
			.orElseThrow(() -> new CustomException(ErrorCode.RANK_SNAPSHOT_NOT_FOUND));
	}

	private List<RankEntryResponse> findRankList(RankSnapshot rankSnapshot, String keyword, Pageable pageable) {
		if (keyword == null || keyword.isBlank()) {
			return rankSnapshotEntryRepository.findByRankSnapshotOrderByPositionAsc(rankSnapshot, pageable)
				.stream()
				.map(RankEntryResponse::from)
				.toList();
		}

		return rankSnapshotEntryRepository
			.findByRankSnapshotAndMemberNameContainingOrderByPositionAsc(rankSnapshot, keyword, pageable)
			.stream()
			.map(RankEntryResponse::from)
			.toList();
	}
}
