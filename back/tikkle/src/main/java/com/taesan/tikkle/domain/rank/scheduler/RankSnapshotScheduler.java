package com.taesan.tikkle.domain.rank.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.taesan.tikkle.domain.rank.service.RankSnapshotService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RankSnapshotScheduler {

	private final RankSnapshotService rankSnapshotService;

	@Scheduled(cron = "0 0 * * * *")
	public void generateRankSnapshot() {
		rankSnapshotService.generateSnapshot();
	}
}
