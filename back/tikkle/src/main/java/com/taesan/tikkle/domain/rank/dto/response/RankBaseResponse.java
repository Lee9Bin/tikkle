package com.taesan.tikkle.domain.rank.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class RankBaseResponse {

	public List<RankEntryResponse> rankList;

	public static RankBaseResponse from(List<RankEntryResponse> rankList) {
		return RankBaseResponse.builder()
			.rankList(rankList)
			.build();
	}
}
