package com.taesan.tikkle.domain.member.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.taesan.tikkle.domain.member.entity.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member, UUID> {
	<T> Optional<T> findById(UUID id, Class<T> type);

	Optional<Member> findByEmail(String email);

	Optional<Member> findByIdAndDeletedAtIsNull(UUID memberId);
}
