package com.taesan.tikkle.domain.chat.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChatResponse {
	private UUID senderId;
	private String content;
	private LocalDateTime timestamp;
}
