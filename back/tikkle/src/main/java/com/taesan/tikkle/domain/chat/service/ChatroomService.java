package com.taesan.tikkle.domain.chat.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.f4b6a3.ulid.UlidCreator;
import com.taesan.tikkle.domain.board.entity.Board;
import com.taesan.tikkle.domain.board.repository.BoardRepository;
import com.taesan.tikkle.domain.chat.dto.request.CreateChatroomRequest;
import com.taesan.tikkle.domain.chat.dto.reseponse.CreateChatroomResponse;
import com.taesan.tikkle.domain.chat.dto.response.DetailChatroomResponse;
import com.taesan.tikkle.domain.chat.entity.Chat;
import com.taesan.tikkle.domain.chat.entity.Chatroom;
import com.taesan.tikkle.domain.chat.repository.ChatRepository;
import com.taesan.tikkle.domain.chat.repository.ChatroomRepository;
import com.taesan.tikkle.domain.member.entity.Member;
import com.taesan.tikkle.domain.member.repository.MemberRepository;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;

@Service
public class ChatroomService {
	@Autowired
	private static ChatroomRepository chatroomRepository;

	@Autowired
	private static ChatRepository chatRepository;

	@Autowired
	private static BoardRepository boardRepository;

	@Autowired
	private static MemberRepository memberRepository;

	@Transactional
	public CreateChatroomResponse createChatroom(CreateChatroomRequest request) {
		UUID boardId = request.getBoardId();
		Board board = boardRepository.findById(boardId).orElseThrow(EntityNotFoundException::new);
		Member writer = board.getMember();
		if (chatroomRepository.findByBoardId(boardId).isPresent()) {
			throw new EntityExistsException();
		}
		// TODO : 현재 세션에 로그인한 회원 가져와야함(첫번째 writer 삭제후 기입)
		Chatroom chatroom = new Chatroom(board, writer, writer);
		chatroomRepository.save(chatroom);
		return new CreateChatroomResponse(chatroom.getId());
	}

	@Transactional(readOnly = true)
	public List<DetailChatroomResponse> getChatrooms() {
		// TODO : 세션 로그인 아이디 가져오기, 현재는 가상 memberId
		UUID memberId = UlidCreator.getMonotonicUlid().toUuid();
		List<Chatroom> writerRooms = chatroomRepository.findByWriterId(memberId);
		List<Chatroom> performerRooms = chatroomRepository.findByPerformerId(memberId);
		List<DetailChatroomResponse> responses = new ArrayList<>();
		// TODO : 서비스단 메서드 하나 생성하여 리팩토링 필요
		for (Chatroom chatroom : writerRooms) {
			Chat lastChat = chatRepository.findTopByRoomIdOrderByTimestampDesc(chatroom.getId());
			if (lastChat != null) {
				Member lastSender = memberRepository.findById(lastChat.getSenderId())
					.orElseThrow(EntityNotFoundException::new);
				responses.add(
					new DetailChatroomResponse(chatroom.getId(), chatroom.getPerformer().getNickname(),
						lastSender.getNickname(), lastChat.getContent()));
			} else {
				responses.add(new DetailChatroomResponse(chatroom.getId(), chatroom.getPerformer().getNickname()));
			}
		}
		for (Chatroom chatroom : performerRooms) {
			Chat lastChat = chatRepository.findTopByRoomIdOrderByTimestampDesc(chatroom.getId());
			if (lastChat != null) {
				Member lastSender = memberRepository.findById(lastChat.getSenderId())
					.orElseThrow(EntityNotFoundException::new);
				responses.add(
					new DetailChatroomResponse(chatroom.getId(), chatroom.getWriter().getNickname(),
						lastSender.getNickname(), lastChat.getContent()));
			} else {
				responses.add(new DetailChatroomResponse(chatroom.getId(), chatroom.getWriter().getNickname()));
			}
		}
		return responses;
	}
}
