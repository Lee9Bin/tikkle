package com.taesan.tikkle.domain.rank.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.taesan.tikkle.domain.rank.service.RankService;
import com.taesan.tikkle.domain.rank.service.RankSnapshotService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class RankSnapshotScheduler {

	private final RankSnapshotService rankSnapshotService;
	private final RankService rankService;

	@Scheduled(cron = "0 0 * * * *")
	public void generateRankSnapshot() {
		try {
			rankSnapshotService.generateSnapshot();
		} catch (Exception exception) {
			log.error("랭킹 스냅샷 생성에 실패했습니다.", exception);
			return;
		}

		try {
			rankService.refreshTopRanksCache();
		} catch (Exception exception) {
			log.warn("상위 랭킹 캐시 갱신에 실패했습니다.", exception);
		}
	}
}
