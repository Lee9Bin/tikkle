package com.taesan.tikkle.domain.rank.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.StreamSupport;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.taesan.tikkle.domain.rank.dto.query.RankSnapshotSource;
import com.taesan.tikkle.domain.rank.entity.RankSnapshot;
import com.taesan.tikkle.domain.rank.entity.RankSnapshotEntry;
import com.taesan.tikkle.domain.rank.entity.RankSnapshotStatus;
import com.taesan.tikkle.domain.rank.repository.RankSnapshotEntryRepository;
import com.taesan.tikkle.domain.rank.repository.RankSnapshotQueryRepository;
import com.taesan.tikkle.domain.rank.repository.RankSnapshotRepository;

@ExtendWith(MockitoExtension.class)
public class RankSnapshotServiceTest {

	@Mock
	private RankSnapshotRepository rankSnapshotRepository;

	@Mock
	private RankSnapshotQueryRepository rankSnapshotQueryRepository;

	@Mock
	private RankSnapshotEntryRepository rankSnapshotEntryRepository;

	@InjectMocks
	private RankSnapshotService rankSnapshotService;

	private RankSnapshot rankSnapshot;
	private List<RankSnapshotSource> rankSnapshotSources;

	@BeforeEach
	public void setUp() {
		rankSnapshot = RankSnapshot.create();
		rankSnapshotSources = List.of(
			new RankSnapshotSource(UUID.randomUUID(), "김티끌", 1000L, 3L),
			new RankSnapshotSource(UUID.randomUUID(), "이티끌", 1000L, 3L),
			new RankSnapshotSource(UUID.randomUUID(), "박티끌", 900L, 5L)
		);

		when(rankSnapshotRepository.save(any(RankSnapshot.class))).thenReturn(rankSnapshot);
		when(rankSnapshotQueryRepository.findRankSnapshotSources()).thenReturn(rankSnapshotSources);
	}

	@Test
	public void 랭킹_엔트리_저장이_끝나면_스냅샷을_완료_상태로_변경한다() {
		// When
		rankSnapshotService.generateSnapshot();

		// Then
		assertEquals(RankSnapshotStatus.COMPLETED, rankSnapshot.getStatus());
	}

	@Test
	public void position은_랭킹_생성_데이터_순서대로_1부터_부여된다() {
		// When
		List<RankSnapshotEntry> rankSnapshotEntries = generateAndCaptureRankSnapshotEntries();

		// Then
		assertAll(
			() -> assertEquals(1, rankSnapshotEntries.get(0).getPosition()),
			() -> assertEquals(2, rankSnapshotEntries.get(1).getPosition()),
			() -> assertEquals(3, rankSnapshotEntries.get(2).getPosition())
		);
	}

	@Test
	public void 포인트와_거래횟수가_같으면_같은_rank를_부여한다() {
		// When
		List<RankSnapshotEntry> rankSnapshotEntries = generateAndCaptureRankSnapshotEntries();

		// Then
		assertAll(
			() -> assertEquals(1, rankSnapshotEntries.get(0).getRank()),
			() -> assertEquals(1, rankSnapshotEntries.get(1).getRank())
		);
	}

	@Test
	public void 동점자가_있으면_다음_rank는_동점자_수를_반영해_건너뛴다() {
		// When
		List<RankSnapshotEntry> rankSnapshotEntries = generateAndCaptureRankSnapshotEntries();

		// Then
		assertEquals(3, rankSnapshotEntries.get(2).getRank());
	}

	@SuppressWarnings("unchecked")
	private List<RankSnapshotEntry> generateAndCaptureRankSnapshotEntries() {
		rankSnapshotService.generateSnapshot();

		ArgumentCaptor<Iterable<RankSnapshotEntry>> rankSnapshotEntriesCaptor = ArgumentCaptor.forClass(Iterable.class);
		verify(rankSnapshotEntryRepository).saveAll(rankSnapshotEntriesCaptor.capture());

		return StreamSupport
			.stream(rankSnapshotEntriesCaptor.getValue().spliterator(), false)
			.toList();
	}
}
