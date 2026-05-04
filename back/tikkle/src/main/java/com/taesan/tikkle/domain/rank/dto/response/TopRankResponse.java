package com.taesan.tikkle.domain.rank.dto.response;

import java.util.UUID;

import com.taesan.tikkle.domain.rank.entity.RankSnapshotEntry;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopRankResponse {

	private UUID memberId;
	private int rank;
	private String memberName;
	private long rankingPoint;

	public static TopRankResponse from(RankSnapshotEntry rankSnapshotEntry) {
		return TopRankResponse.builder()
			.memberId(rankSnapshotEntry.getMemberId())
			.rank(rankSnapshotEntry.getRank())
			.memberName(rankSnapshotEntry.getMemberName())
			.rankingPoint(rankSnapshotEntry.getRankingPoint())
			.build();
	}
}
