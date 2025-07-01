package com.itwillbs.factron.service.lotStructure;

import com.itwillbs.factron.common.component.AuthorizationChecker;
import com.itwillbs.factron.entity.Lot;
import com.itwillbs.factron.entity.LotStructure;
import com.itwillbs.factron.repository.lot.LotStructureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LotStructureServiceImpl implements LotStructureService {

    private final LotStructureRepository lotStructureRepository;
    private final AuthorizationChecker authorizationChecker;

    @Override
    @Transactional
    public Void linkLotStructure(Lot parent, List<Lot> child) {

        if (!authorizationChecker.hasAnyAuthority("ATH003", "ATH006", "ATH007")) {
            throw new SecurityException("권한이 없습니다.");
        }

        List<LotStructure> treeList = new ArrayList<>();

        child.forEach(lot -> {

            LotStructure lotStructure = LotStructure.builder()
                    .parentLot(parent)
                    .childLot(lot)
                    .build();

            treeList.add(lotStructure);
        });

        lotStructureRepository.saveAll(treeList);

        return null;

    }

}
