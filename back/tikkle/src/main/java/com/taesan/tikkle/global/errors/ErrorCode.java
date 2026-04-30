package com.taesan.tikkle.global.errors;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

	/*
		TODO: 개발 과정에서 지속적인 추가 필요
	 */

	AUTH_ILL_REQUEST(HttpStatus.BAD_REQUEST, 1701, "요청이 유효하지 않습니다."),
	AUTH_ILL_REFRESH(HttpStatus.BAD_REQUEST, 1710, "올바르지 않은 Refresh Token 입니다."),
	AUTH_JWT_EXPIRED(HttpStatus.BAD_REQUEST, 1711, "올바르지 않은 JWT 형식입니다."),
	MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, 1704, "존재하지 않는 회원입니다."),
	MEMBER_IMAGE_NOT_FOUND(HttpStatus.BAD_REQUEST, 1720, "회원 프로필 이미지가 존재하지 않습니다."),
	APPOINTMENT_NOT_AUTHORIZED(HttpStatus.BAD_REQUEST, 1200, "약속 참여자가 아닙니다."),
	APPOINTMENT_NOT_FOUND(HttpStatus.BAD_REQUEST, 1204, "약속이 존재하지 않습니다."),
	CHATROOM_NOT_FOUND(HttpStatus.BAD_REQUEST, 1404, "채팅방이 존재하지 않습니다."),
	CHATROOM_NOT_AUTHORIZED(HttpStatus.BAD_REQUEST, 1400, "채팅방 참여자가 아닙니다."),
	CHATROOM_EXISTS(HttpStatus.BAD_REQUEST, 1409, "이미 존재한 채팅방입니다."),
	BOARD_NOT_FOUND(HttpStatus.BAD_REQUEST, 1304, "게시글이 존재하지 않습니다."),
	Board_DELETED(HttpStatus.BAD_REQUEST,1305,"삭제된 공고입니다."),
	RATE_NOT_FOUND(HttpStatus.BAD_REQUEST, 2004, "정의되지 않은 환율입니다."),
	RATE_NOT_EXIST(HttpStatus.BAD_REQUEST, 2005, "정의된 환율이 없습니다."),
	RATE_INVALID(HttpStatus.BAD_REQUEST, 2006, "최신 환율이 아닙니다. 다시 시도 해주세요."),
	RANK_SNAPSHOT_NOT_FOUND(HttpStatus.BAD_REQUEST, 2104, "랭킹 스냅샷이 존재하지 않습니다."),
	ACCOUNT_NOT_FOUND(HttpStatus.BAD_REQUEST, 1104, "사용자의 계좌가 없습니다."),
	ACCOUNT_INSUFFICIENT_BALANCE(HttpStatus.BAD_REQUEST, 1105, "사용자의 잔고가 부족합니다.");
;
	private final HttpStatus status;
	private final int code;
	private final String message;

}
