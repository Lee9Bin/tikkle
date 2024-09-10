package com.taesan.tikkle.domain.rate.entity;

import java.util.List;
import java.util.UUID;

import com.github.f4b6a3.ulid.UlidCreator;
import com.taesan.tikkle.domain.account.entity.ExchangeLog;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "rates")
public class Rate {
	@Id
	@Column(columnDefinition = "BINARY(16)")
	private UUID id = UlidCreator.getMonotonicUlid().toUuid();

	@Column(name = "time_to_rank")
	private Double timeToRank;

	@Column(name = "rank_to_time")
	private Double rankToTime;

	@OneToMany(mappedBy = "rate")
	private List<ExchangeLog> exchangeLogs;
}
