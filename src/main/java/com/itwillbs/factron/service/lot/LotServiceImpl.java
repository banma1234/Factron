package com.itwillbs.factron.service.lot;

import com.itwillbs.factron.common.component.AuthorizationChecker;
import com.itwillbs.factron.dto.lot.RequestLotUpdateDTO;
import com.itwillbs.factron.dto.lot.ResponseLotDTO;
import com.itwillbs.factron.dto.lotHistory.RequestLotHistoryDTO;
import com.itwillbs.factron.entity.Lot;
import com.itwillbs.factron.entity.WorkOrder;
import com.itwillbs.factron.mapper.lot.LotMapper;
import com.itwillbs.factron.repository.lot.LotRepository;
import com.itwillbs.factron.service.lotHistory.LotHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LotServiceImpl implements LotService {

    private final LotMapper lotMapper;
    private final LotHistoryService lotHistoryService;
    private final LotRepository lotRepository;
    private final AuthorizationChecker authorizationChecker;

    /**
     * 같은 조건의 LOT번호 개수 반환
     * */
    @Override
    public Long getLotSequence (Map<String, String> map) {
        return lotMapper.getLotSequence(map);
    }

    @Override
    public List<ResponseLotDTO> getLotById(String lotId) {

        List<Lot> lotList;

        if(lotId == null || lotId.isEmpty()) {
            lotList = lotRepository.findAll();
        } else {
            lotList = lotRepository
                    .findByIdContaining(lotId)
                    .orElseThrow(() -> new NoSuchElementException("해당하는 Lot 번호가 없습니다."));
        }

        return toLotDTOList(lotList);
    }

    @Override
    @Transactional
    public Void updateLotQuantity (List<RequestLotUpdateDTO> materialList) {

        if (!authorizationChecker.hasAnyAuthority("ATH003", "ATH007")) {
            throw new SecurityException("권한이 없습니다.");
        }

        if(materialList.isEmpty()) {
            throw new NoSuchElementException("조회할 자재 정보가 없습니다.");
        }

        Map<String, List<Lot>> map = new HashMap<>();

        materialList.forEach(target -> {

            List<Lot> LotList = new ArrayList<>();
            List<Lot> allLots = lotMapper.getInboundLotById(target);

            Long sum = 0L;
            for (Lot lot : allLots) {
                if (sum > target.getQuantity()) {
                    break;
                }

                sum += lot.getQuantity();
                LotList.add(lot);
            }

            if(sum < target.getQuantity()) {
                throw new IllegalArgumentException("재고보다 많은 품목을 출고할 수 없습니다.");
            }

            if(target.getMaterial_id() == null || target.getMaterial_id().isEmpty()) {
                map.put(target.getItem_id(), LotList);
            } else {
                map.put(target.getMaterial_id(), LotList);
            }

        });

        materialList.forEach(target -> {
            Long targetQuantity = target.getQuantity();

            String key = (target.getMaterial_id() == null || target.getMaterial_id().isEmpty())
                    ? target.getItem_id()
                    : target.getMaterial_id();

            List<Lot> lots = map.getOrDefault(key, Collections.emptyList());

            updateLotsAndRecordHistory(lots, targetQuantity, target.getWork_order_id());
        });

        return null;
    }

    private void updateLotsAndRecordHistory(List<Lot> lots, Long targetQuantity, WorkOrder workOrderId) {

        for (Lot lot : lots) {
            Long originalQuantity = lot.getQuantity();

            if (originalQuantity == 0) {
                continue;
            }

            Long updatedQuantity = originalQuantity - targetQuantity;
            lot.updateQuantity(Math.max(0, updatedQuantity));

            lotMapper.updateLotQuantity(lot);
            lotHistoryService.addHistory(RequestLotHistoryDTO.builder()
                    .lot_id(lot.getId())
                    .quantity(Math.min(originalQuantity, targetQuantity))
                    .work_order(workOrderId)
                    .created_at(LocalDateTime.now())
                    .build()
            );

            targetQuantity -= originalQuantity;

            if (targetQuantity <= 0) {
                break;
            }
        }
    }

    private List<ResponseLotDTO> toLotDTOList(List<Lot> lotList) {

        return lotList.stream()
                .map(ResponseLotDTO :: fromEntity)
                .toList();
    }

}
