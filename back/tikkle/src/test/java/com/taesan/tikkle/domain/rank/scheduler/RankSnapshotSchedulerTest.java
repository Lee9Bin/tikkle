package com.taesan.tikkle.domain.rank.scheduler;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.taesan.tikkle.domain.rank.service.RankService;
import com.taesan.tikkle.domain.rank.service.RankSnapshotService;

@ExtendWith(MockitoExtension.class)
public class RankSnapshotSchedulerTest {

	@Mock
	private RankSnapshotService rankSnapshotService;

	@Mock
	private RankService rankService;

	@InjectMocks
	private RankSnapshotScheduler rankSnapshotScheduler;

	@Test
	public void 랭킹_스냅샷_생성이_성공하면_상위_랭킹_캐시를_갱신한다() {
		// When
		rankSnapshotScheduler.generateRankSnapshot();

		// Then
		verify(rankSnapshotService).generateSnapshot();
		verify(rankService).refreshTopRanksCache();
	}

	@Test
	public void 랭킹_스냅샷_생성_실패시_스케줄러는_정상_종료된다() {
		// Given
		doThrow(new RuntimeException("랭킹 스냅샷 생성 실패"))
			.when(rankSnapshotService)
			.generateSnapshot();

		// When & Then
		assertDoesNotThrow(() -> rankSnapshotScheduler.generateRankSnapshot());
		verify(rankSnapshotService).generateSnapshot();
		verify(rankService, never()).refreshTopRanksCache();
	}

	@Test
	public void 상위_랭킹_캐시_갱신_실패시_스케줄러는_정상_종료된다() {
		// Given
		doThrow(new RuntimeException("상위 랭킹 캐시 갱신 실패"))
			.when(rankService)
			.refreshTopRanksCache();

		// When & Then
		assertDoesNotThrow(() -> rankSnapshotScheduler.generateRankSnapshot());
		verify(rankSnapshotService).generateSnapshot();
		verify(rankService).refreshTopRanksCache();
	}
}
