package com.taesan.tikkle.domain.rank.entity;

import java.util.UUID;

import com.github.f4b6a3.ulid.UlidCreator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
	name = "rank_snapshot_entries",
	indexes = {
		@Index(name = "idx_rank_snapshot_entries_snapshot_position", columnList = "rank_snapshot_id, position"),
		@Index(name = "idx_rank_snapshot_entries_snapshot_member", columnList = "rank_snapshot_id, member_id")
	}
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RankSnapshotEntry {

	@Id
	@Column(columnDefinition = "BINARY(16)")
	private UUID id = UlidCreator.getMonotonicUlid().toUuid();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "rank_snapshot_id", nullable = false)
	private RankSnapshot rankSnapshot;

	@Column(name = "member_id", nullable = false, columnDefinition = "BINARY(16)")
	private UUID memberId;

	@Column(nullable = false, length = 64)
	private String memberName;

	@Column(nullable = false)
	private long rankingPoint;

	@Column(nullable = false)
	private long tradeCount;

	@Column(nullable = false)
	private int position;

	@Column(nullable = false)
	private int rank;

	@Builder(access = AccessLevel.PRIVATE)
	private RankSnapshotEntry(RankSnapshot rankSnapshot, UUID memberId, String memberName, long rankingPoint,
		long tradeCount, int position, int rank) {
		this.rankSnapshot = rankSnapshot;
		this.memberId = memberId;
		this.memberName = memberName;
		this.rankingPoint = rankingPoint;
		this.tradeCount = tradeCount;
		this.position = position;
		this.rank = rank;
	}

	public static RankSnapshotEntry create(RankSnapshot rankSnapshot, UUID memberId, String memberName,
		long rankingPoint, long tradeCount, int position, int rank) {
		return RankSnapshotEntry.builder()
			.rankSnapshot(rankSnapshot)
			.memberId(memberId)
			.memberName(memberName)
			.rankingPoint(rankingPoint)
			.tradeCount(tradeCount)
			.position(position)
			.rank(rank)
			.build();
	}
}
