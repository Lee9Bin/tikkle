package com.taesan.tikkle.domain.rank.controller;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.taesan.tikkle.domain.member.dto.response.MemberRankResponse;
import com.taesan.tikkle.domain.rank.dto.response.RankBaseResponse;
import com.taesan.tikkle.domain.rank.service.RankService;
import com.taesan.tikkle.global.annotations.AuthedUsername;
import com.taesan.tikkle.global.response.ApiResponse;
import com.taesan.tikkle.global.response.PagingResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/rank")
@RequiredArgsConstructor
public class RankController {
	private final RankService rankService;

	@GetMapping
	public ResponseEntity<ApiResponse<PagingResponse<MemberRankResponse>>> getMemberRanking(
		@AuthedUsername UUID username, Pageable pageable) {

		PagingResponse<MemberRankResponse> responseData = rankService.getRankList(username, pageable);

		ApiResponse<PagingResponse<MemberRankResponse>> response =
			ApiResponse.success("랭킹 조회에 성공했습니다.", responseData);

		return ResponseEntity.ok(response);
	}

	@GetMapping("search")
	public ResponseEntity<ApiResponse<RankBaseResponse>> getSearchRanking(@RequestParam String keyword) {
		ApiResponse<RankBaseResponse> response = ApiResponse.success("랭킹 조회에 성공했습니다",
			rankService.getSearchRankList(keyword));
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
}
