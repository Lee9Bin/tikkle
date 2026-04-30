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
public class RankResponse extends RankBaseResponse {

	private RankEntryResponse myRank;

	public static RankResponse of(List<RankEntryResponse> rankList, RankEntryResponse myRank) {
		return RankResponse.builder()
			.rankList(rankList)
			.myRank(myRank)
			.build();
	}
}
