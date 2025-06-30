package com.itwillbs.factron.service.lot;

import com.itwillbs.factron.dto.lot.RequestLotUpdateDTO;
import com.itwillbs.factron.entity.Lot;
import com.itwillbs.factron.mapper.lot.LotMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LotServiceImpl implements LotService {

    private final LotMapper lotMapper;

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
        List<Lot> LotList = new ArrayList<>();

        materialList.forEach(target -> {

            LotList.clear();
            List<Lot> allLots = lotMapper.getInboundLotById(target);


            Long sum = 0L;
            for (Lot lot : allLots) {
                if (sum > target.getQuantity()) {
                    break;
                }


                sum += lot.getQuantity();
                LotList.add(lot);
            }

            map.put(target.getMaterial_id(), LotList);
        });

        materialList.forEach(target -> {

            Long targetQuantity = target.getQuantity();

            for( Lot lot : map.get(target.getMaterial_id())) {
                Long originalQuantity = lot.getQuantity();
                Long updatedQuantity = lot.getQuantity() - targetQuantity;
                lot.updateQuantity(Math.max(0, updatedQuantity));

                lotMapper.updateLotQuantity(lot);

                targetQuantity -= originalQuantity;

                if (targetQuantity <= 0) {
                    break;
                }
            }
        });

        return null;
    }

}
