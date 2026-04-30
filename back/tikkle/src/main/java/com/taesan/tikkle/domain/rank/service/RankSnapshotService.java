package com.taesan.tikkle.domain.rank.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taesan.tikkle.domain.rank.dto.query.RankSnapshotSource;
import com.taesan.tikkle.domain.rank.entity.RankSnapshot;
import com.taesan.tikkle.domain.rank.entity.RankSnapshotEntry;
import com.taesan.tikkle.domain.rank.repository.RankSnapshotEntryRepository;
import com.taesan.tikkle.domain.rank.repository.RankSnapshotQueryRepository;
import com.taesan.tikkle.domain.rank.repository.RankSnapshotRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RankSnapshotService {

	private final RankSnapshotRepository rankSnapshotRepository;
	private final RankSnapshotQueryRepository rankSnapshotQueryRepository;
	private final RankSnapshotEntryRepository rankSnapshotEntryRepository;

	/**
	 * 생성 중인 스냅샷이 조회되지 않도록 엔트리 저장이 끝난 뒤 완료 상태로 전환한다.
	 */
	@Transactional
	public void generateSnapshot() {
		RankSnapshot rankSnapshot = rankSnapshotRepository.save(RankSnapshot.create());
		List<RankSnapshotSource> rankSnapshotSources = rankSnapshotQueryRepository.findRankSnapshotSources();
		List<RankSnapshotEntry> rankSnapshotEntries = createRankSnapshotEntries(rankSnapshot, rankSnapshotSources);

		rankSnapshotEntryRepository.saveAll(rankSnapshotEntries);
		rankSnapshot.complete();
	}

	/**
	 * rank는 포인트와 거래 횟수 기준으로 계산하고, position은 조회 순서대로 부여한다.
	 */
	private List<RankSnapshotEntry> createRankSnapshotEntries(RankSnapshot rankSnapshot,
		List<RankSnapshotSource> sortedRankSnapshotSources) {
		List<RankSnapshotEntry> rankSnapshotEntries = new ArrayList<>();
		int currentRank = 1;

		for (int index = 0; index < sortedRankSnapshotSources.size(); index++) {
			RankSnapshotSource currentRankSnapshotSource = sortedRankSnapshotSources.get(index);
			int position = index + 1;

			if (index > 0 && !isTie(sortedRankSnapshotSources.get(index - 1), currentRankSnapshotSource)) {
				currentRank = position;
			}

			rankSnapshotEntries.add(
				createRankSnapshotEntry(rankSnapshot, currentRankSnapshotSource, position, currentRank));
		}

		return rankSnapshotEntries;
	}

	private RankSnapshotEntry createRankSnapshotEntry(RankSnapshot rankSnapshot, RankSnapshotSource rankSnapshotSource,
		int position, int rank) {
		return RankSnapshotEntry.create(
			rankSnapshot,
			rankSnapshotSource.getMemberId(),
			rankSnapshotSource.getMemberName(),
			rankSnapshotSource.getRankingPoint(),
			rankSnapshotSource.getTradeCount(),
			position,
			rank
		);
	}

	private boolean isTie(RankSnapshotSource previousRankSnapshotSource,
		RankSnapshotSource currentRankSnapshotSource) {
		return previousRankSnapshotSource.getRankingPoint() == currentRankSnapshotSource.getRankingPoint()
			&& previousRankSnapshotSource.getTradeCount() == currentRankSnapshotSource.getTradeCount();
	}
}
