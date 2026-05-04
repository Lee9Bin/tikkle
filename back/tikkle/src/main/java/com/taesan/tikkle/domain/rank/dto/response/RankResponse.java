package com.taesan.tikkle.domain.rank.dto.response;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RankResponse {

	private List<RankEntryResponse> rankList;
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

	public static RankResponse empty(Pageable pageable) {
		return RankResponse.of(Page.empty(pageable), null);
	}
}
