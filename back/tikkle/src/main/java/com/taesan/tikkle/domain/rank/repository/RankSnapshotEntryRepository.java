package com.taesan.tikkle.domain.rank.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.taesan.tikkle.domain.rank.entity.RankSnapshot;
import com.taesan.tikkle.domain.rank.entity.RankSnapshotEntry;

public interface RankSnapshotEntryRepository extends JpaRepository<RankSnapshotEntry, UUID> {

	Page<RankSnapshotEntry> findByRankSnapshotOrderByPositionAsc(RankSnapshot rankSnapshot, Pageable pageable);

	Page<RankSnapshotEntry> findByRankSnapshotAndMemberNameContainingOrderByPositionAsc(
		RankSnapshot rankSnapshot,
		String memberName,
		Pageable pageable
	);
}
