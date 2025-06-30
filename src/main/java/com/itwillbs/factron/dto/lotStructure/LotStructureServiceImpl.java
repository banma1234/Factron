package com.itwillbs.factron.dto.lotStructure;

import com.itwillbs.factron.dto.lot.RequestProcessLotDTO;
import com.itwillbs.factron.entity.Lot;
import com.itwillbs.factron.entity.LotHistory;
import com.itwillbs.factron.repository.lot.LotHistoryRepository;
import com.itwillbs.factron.repository.lot.LotRepository;
import com.itwillbs.factron.repository.lot.LotStructureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LotStructureServiceImpl implements LotStructureService {

    private final LotStructureRepository lotStructureRepository;
    private final LotHistoryRepository lotHistoryRepository;
    private final LotRepository lotRepository;

    @Override
    @Transactional
    public Void CreateLotStructure(RequestProcessLotDTO reqProcessLotDTO, Lot lot) {

        List<LotHistory> lotHistoryList = lotHistoryRepository
                .findByWorkOrderIdOrderByCreatedAtDesc(reqProcessLotDTO.getWork_order_id())
                .orElseThrow(() -> new NoSuchElementException("해당하는 Lot history가 존재하지 않습니다."));

        LotHistory firstElement = lotHistoryList.getFirst();

//        if (firstElement.getLot().getId().matches("^\\d{8}-INB-\\d{4}$") {
//
//
//        }

        return null;
    }
}
