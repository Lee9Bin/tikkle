package com.taesan.tikkle.domain.rank.dto.query;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RankSnapshotSource {

	private UUID memberId;
	private String memberName;
	private long rankingPoint;
	private long tradeCount;
}
