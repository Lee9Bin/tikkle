package com.taesan.tikkle.domain.member.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.taesan.tikkle.domain.board.entity.Board;
import com.taesan.tikkle.domain.board.repository.BoardRepository;
import com.taesan.tikkle.domain.file.service.FileService;
import com.taesan.tikkle.domain.member.dto.response.AllMemberBoardResponse;
import com.taesan.tikkle.domain.member.dto.response.MemberBoardResponse;
import com.taesan.tikkle.domain.member.dto.response.MemberRankResponse;
import com.taesan.tikkle.domain.member.dto.response.MemberResponse;
import com.taesan.tikkle.domain.member.entity.Member;
import com.taesan.tikkle.domain.member.repository.MemberRepository;
import com.taesan.tikkle.global.errors.ErrorCode;
import com.taesan.tikkle.global.exceptions.CustomException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;
	private final BoardRepository boardRepository;
	private final FileService fileService;

	/*
		TODO: 에러 코드 정리 필요
	 */
	public Member getMember(UUID id) {
		Member member = memberRepository.findById(id)
			.orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

		return member;
	}

	public MemberResponse getMemberResponse(UUID id) {
		Member member =
			memberRepository.findById(id)
				.orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

		byte[] profileImage = fileService.getProfileImage(member.getId());

		return MemberResponse.from(member, profileImage);
	}

	public Page<MemberRankResponse> findMemberRankings(Pageable pageable) {
		return memberRepository.findMemberRankings(pageable);
	}

	public AllMemberBoardResponse getMemberBoard(UUID username) {
		if (memberRepository.findById(username).isEmpty())
			throw new CustomException(ErrorCode.MEMBER_NOT_FOUND);
		List<Board> boards = boardRepository.findByMemberIdAndDeletedAtIsNullOrderByCreatedAtDesc(username);
		List<MemberBoardResponse> postedPosts = new ArrayList<>();
		List<MemberBoardResponse> activePosts = new ArrayList<>();
		List<MemberBoardResponse> donePosts = new ArrayList<>();
		for (Board board : boards) {
			MemberBoardResponse response = new MemberBoardResponse(board.getId(), board.getTitle(), board.getContent(),
				board.getStatus(), board.getTime(), board.getCreatedAt());
			switch (response.getStatus()) {
				case "진행전" -> postedPosts.add(response);
				case "진행중" -> activePosts.add(response);
				case "완료됨" -> donePosts.add(response);
			}
		}
		return new AllMemberBoardResponse(postedPosts, activePosts, donePosts);
	}
}
