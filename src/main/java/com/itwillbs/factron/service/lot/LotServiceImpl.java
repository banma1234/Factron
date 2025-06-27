package com.itwillbs.factron.service.lot;

import com.itwillbs.factron.dto.lot.RequestLotUpdateDTO;
import com.itwillbs.factron.entity.Lot;
import com.itwillbs.factron.mapper.lot.LotMapper;
import com.itwillbs.factron.repository.lot.LotRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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

        // Map<String, List<Lot>> map = new HashMap<>();

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

            // map.put(target.getMaterial_id(), LotList);
        });



        return null;
    }

}
