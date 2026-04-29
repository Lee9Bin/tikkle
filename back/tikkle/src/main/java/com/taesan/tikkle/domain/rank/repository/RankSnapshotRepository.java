package com.taesan.tikkle.domain.rank.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.taesan.tikkle.domain.rank.entity.RankSnapshot;
import com.taesan.tikkle.domain.rank.entity.RankSnapshotStatus;

public interface RankSnapshotRepository extends JpaRepository<RankSnapshot, UUID> {

	Optional<RankSnapshot> findTopByStatusOrderByCreatedAtDesc(RankSnapshotStatus status);
}
