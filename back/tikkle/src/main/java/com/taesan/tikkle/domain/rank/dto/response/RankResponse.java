package com.taesan.tikkle.domain.rank.dto.response;

import org.springframework.data.domain.Page;

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
	private int page;
	private int size;
	private long totalElements;
	private int totalPages;
	private boolean hasNext;

	public static RankResponse of(Page<RankEntryResponse> rankList, RankEntryResponse myRank) {
		return RankResponse.builder()
			.rankList(rankList.getContent())
			.myRank(myRank)
			.page(rankList.getNumber())
			.size(rankList.getSize())
			.totalElements(rankList.getTotalElements())
			.totalPages(rankList.getTotalPages())
			.hasNext(rankList.hasNext())
			.build();
	}
}
