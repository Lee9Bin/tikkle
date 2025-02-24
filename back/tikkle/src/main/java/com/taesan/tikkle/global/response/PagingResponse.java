package com.taesan.tikkle.global.response;

import java.util.List;

import org.springframework.data.domain.Page;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class PagingResponse<T> {

	private List<T> content;
	private int currentPage;
	private int pageSize;
	private long totalElements;
	private int totalPages;

	public static <T> PagingResponse<T> from(Page<T> page){
		return PagingResponse.<T>builder()
			.content(page.getContent())
			.currentPage(page.getNumber())
			.pageSize(page.getSize())
			.totalElements(page.getTotalElements())
			.totalPages(page.getTotalPages())
			.build();
	}
}
