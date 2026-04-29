package com.taesan.tikkle.domain.rank.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.taesan.tikkle.domain.rank.dto.query.RankSnapshotSource;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RankSnapshotQueryRepository {

	private final EntityManager entityManager;

	/**
	 * 랭킹의 거래 횟수는 공고를 처리한 횟수이므로 수신 계좌(recAccount) 기준으로 집계한다.
	 */
	public List<RankSnapshotSource> findRankSnapshotSources() {
		return entityManager.createQuery("""
				SELECT new com.taesan.tikkle.domain.rank.dto.query.RankSnapshotSource(
					m.id,
					m.name,
					a.rankingPoint,
					COUNT(tl.id)
				)
				FROM Member m
				JOIN Account a ON m.id = a.member.id
				LEFT JOIN TradeLog tl ON a.id = tl.recAccount.id
				GROUP BY m.id, m.name, a.rankingPoint
				""", RankSnapshotSource.class)
			.getResultList();
	}
}
