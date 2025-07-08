package com.itwillbs.factron.service.lotHistory;

import com.itwillbs.factron.common.component.AuthorizationChecker;
import com.itwillbs.factron.dto.lotHistory.RequestLotHistoryDTO;
import com.itwillbs.factron.entity.Lot;
import com.itwillbs.factron.repository.lot.LotHistoryRepository;
import com.itwillbs.factron.repository.lot.LotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LotHistoryServiceImpl implements LotHistoryService {

    private final LotHistoryRepository lotHistoryRepository;
    private final LotRepository lotRepository;
    private final AuthorizationChecker authorizationChecker;

    /**
     * LOT_HISTORY 기록
     * @param reqLotHistoryDTO 입력 대상 DTO
     * @return Void
     * */
    @Override
    @Transactional
    public Void addHistory(RequestLotHistoryDTO reqLotHistoryDTO) {

        // 권한 체크 : 관리자, 재무팀, 생산팀, 작업반장
        if (!authorizationChecker.hasAnyAuthority("ATH003", "ATH005", "ATH006", "ATH007")) {
            throw new SecurityException("권한이 없습니다.");
        }

        Lot lot = lotRepository.findById(reqLotHistoryDTO.getLot_id())
                        .orElseThrow(() -> new NoSuchElementException("해당하는 LOT 번호가 없습니다."));

        lotHistoryRepository.save(reqLotHistoryDTO.toEntity(lot));

        return null;
    }

}
