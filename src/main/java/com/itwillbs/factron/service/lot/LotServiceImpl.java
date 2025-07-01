package com.itwillbs.factron.service.lot;

import com.itwillbs.factron.dto.lot.RequestLotUpdateDTO;
import com.itwillbs.factron.dto.lotHistory.RequestLotHistoryDTO;
import com.itwillbs.factron.entity.Lot;
import com.itwillbs.factron.mapper.lot.LotMapper;
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

    /**
     * 같은 조건의 LOT번호 개수 반환
     * */
    @Override
    public Long getLotSequence (Map<String, String> map) {
        return lotMapper.getLotSequence(map);
    }

    @Override
    @Transactional
    public Void updateInboundLotQuantity (List<RequestLotUpdateDTO> materialList) {

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

            if(target.getMaterial_id() == null || target.getMaterial_id().isEmpty()) {
                map.put(target.getItem_id(), LotList);
            } else {
                map.put(target.getMaterial_id(), LotList);
            }

        });

        materialList.forEach(target -> {

            Long targetQuantity = target.getQuantity();

            for( Lot lot : map.get(target.getMaterial_id())) {
                Long originalQuantity = lot.getQuantity();

                if(originalQuantity == 0) {
                    continue;
                }

                Long updatedQuantity = lot.getQuantity() - targetQuantity;
                lot.updateQuantity(Math.max(0, updatedQuantity));

                lotMapper.updateLotQuantity(lot);
                lotHistoryService.addHistory(RequestLotHistoryDTO.builder()
                        .lot_id(lot.getId())
                        .quantity(Math.min(originalQuantity, targetQuantity))
                        .work_order(target.getWork_order_id())
                        .created_at(LocalDateTime.now())
                        .build()
                );

                targetQuantity -= originalQuantity;

                if (targetQuantity <= 0) {
                    break;
                }
            }
        });

        return null;
    }

}
