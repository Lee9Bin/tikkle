package com.taesan.tikkle.domain.rank.entity;

import java.util.UUID;

import com.github.f4b6a3.ulid.UlidCreator;
import com.taesan.tikkle.global.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "rank_snapshots")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RankSnapshot extends BaseEntity {

	@Id
	@Column(columnDefinition = "BINARY(16)")
	private UUID id = UlidCreator.getMonotonicUlid().toUuid();

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private RankSnapshotStatus status = RankSnapshotStatus.CREATING;

	public static RankSnapshot create() {
		return new RankSnapshot();
	}

	public void complete() {
		this.status = RankSnapshotStatus.COMPLETED;
	}
}
