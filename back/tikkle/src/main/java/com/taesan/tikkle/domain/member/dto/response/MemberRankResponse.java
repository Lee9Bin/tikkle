package com.taesan.tikkle.domain.member.dto.response;

public interface MemberRankResponse {

	String getMemberId();

	String getNickname();

	long getRankingPoint();

	long getTradeCount();

	long getRanking();
}
