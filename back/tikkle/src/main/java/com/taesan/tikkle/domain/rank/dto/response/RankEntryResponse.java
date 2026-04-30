package com.taesan.tikkle.domain.rank.dto.response;

import java.util.UUID;

import com.taesan.tikkle.domain.rank.entity.RankSnapshotEntry;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RankEntryResponse {

	private UUID memberId;
	private int rank;
	private String memberName;
	private long rankingPoint;
	private long tradeCount;

	public static RankEntryResponse from(RankSnapshotEntry rankSnapshotEntry) {
		return RankEntryResponse.builder()
			.memberId(rankSnapshotEntry.getMemberId())
			.rank(rankSnapshotEntry.getRank())
			.memberName(rankSnapshotEntry.getMemberName())
			.rankingPoint(rankSnapshotEntry.getRankingPoint())
			.tradeCount(rankSnapshotEntry.getTradeCount())
			.build();
	}
}
