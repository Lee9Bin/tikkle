package com.taesan.tikkle.domain.member.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.taesan.tikkle.domain.member.dto.response.MemberRankResponse;
import com.taesan.tikkle.domain.member.entity.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member, UUID> {
	<T> Optional<T> findById(UUID id, Class<T> type);

	Optional<Member> findByEmail(String email);

	@Query(value = "SELECT " +
		"m.id as memberId, " +
		"m.name as nickname, " +
		"a.ranking_point as rankingPoint, " +
		"COUNT(tl.id) as tradeCount, " +
		"DENSE_RANK() OVER (ORDER BY a.ranking_point DESC, COUNT(tl.id) DESC) as ranking " +
		"FROM members m " +
		"JOIN accounts a ON m.id = a.member_id " +
		"LEFT JOIN trade_logs tl ON a.id = tl.rec_account_id " +
		"GROUP BY m.id, m.name, a.ranking_point " +
		"ORDER BY a.ranking_point DESC, COUNT(tl.id) DESC",
		countQuery = "SELECT COUNT(DISTINCT m.id) FROM members m",
		nativeQuery = true)
	Page<MemberRankResponse> findMemberRankings(Pageable pageable);

	Optional<Member> findByIdAndDeletedAtIsNull(UUID memberId);
}
