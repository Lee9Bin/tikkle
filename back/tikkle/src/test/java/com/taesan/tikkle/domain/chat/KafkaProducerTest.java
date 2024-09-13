package com.taesan.tikkle.domain.chat;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.Rollback;

import com.taesan.tikkle.domain.chat.entity.ChatMessage;
import com.taesan.tikkle.domain.chat.repository.ChatRepository;
import com.taesan.tikkle.domain.chat.service.KafkaProducer;

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = { "chatroom.test" })  // 임베디드 Kafka 사용
public class KafkaProducerTest {

	@Autowired
	private KafkaProducer kafkaProducer;

	@Autowired
	private ChatRepository chatRepository;

	@Test
	@Rollback(false)  // 트랜잭션 롤백 방지, MongoDB에 실제로 저장되도록 설정
	public void testSendMessage() {
		// Given
		UUID chatroomId = UUID.randomUUID();
		UUID senderId = UUID.randomUUID();
		String content = "Hello, this is a test message.";

		ChatMessage chatMessage = new ChatMessage(chatroomId, senderId, content);

		// When
		kafkaProducer.sendMessage(chatMessage);

		// Then
		var savedChat = chatRepository.findByChatroomId(chatroomId);
		assertThat(savedChat).isNotNull();
		assertThat(savedChat.get().getContent()).isEqualTo(content);
	}
}

