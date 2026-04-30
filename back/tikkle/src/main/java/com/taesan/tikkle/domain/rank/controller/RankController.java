package com.taesan.tikkle.domain.rank.controller;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.taesan.tikkle.domain.rank.dto.response.RankResponse;
import com.taesan.tikkle.domain.rank.service.RankService;
import com.taesan.tikkle.global.annotations.AuthedUsername;
import com.taesan.tikkle.global.response.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/rank")
@RequiredArgsConstructor
public class RankController {
	private final RankService rankService;

	@GetMapping
	public ResponseEntity<ApiResponse<RankResponse>> getRanks(
		@AuthedUsername UUID username,
		@RequestParam(required = false) String keyword,
		Pageable pageable
	) {
		ApiResponse<RankResponse> response = ApiResponse.success("랭킹 조회에 성공했습니다.",
			rankService.getRanks(username, keyword, pageable));
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
}
