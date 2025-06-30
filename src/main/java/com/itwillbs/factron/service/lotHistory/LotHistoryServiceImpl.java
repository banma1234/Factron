package com.itwillbs.factron.service.lotHistory;

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

    @Override
    @Transactional
    public Void addHistory(RequestLotHistoryDTO reqLotHistoryDTO) {

        Lot lot = lotRepository.findById(reqLotHistoryDTO.getLot_id())
                        .orElseThrow(() -> new NoSuchElementException("해당하는 LOT 번호가 없습니다."));

        lotHistoryRepository.save(reqLotHistoryDTO.toEntity(lot));

        return null;
    }
}
