package com.taesan.tikkle.domain.chat.entity;

import java.util.List;
import java.util.UUID;

import com.github.f4b6a3.ulid.UlidCreator;
import com.taesan.tikkle.domain.appointment.entity.Appointment;
import com.taesan.tikkle.domain.board.entity.Board;
import com.taesan.tikkle.domain.member.entity.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "chatrooms")
public class Chatroom {
	@Id
	@Column(columnDefinition = "BINARY(16)")
	private UUID id = UlidCreator.getMonotonicUlid().toUuid();

	@ManyToOne
	@JoinColumn(name = "board_id", nullable = false)
	private Board board;

	@ManyToOne
	@JoinColumn(name = "performer_id", nullable = false)
	private Member performer;

	@ManyToOne
	@JoinColumn(name = "writer_id")
	private Member writer;

	@OneToMany(mappedBy = "room")
	private List<Appointment> appointments;
}

