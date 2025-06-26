package com.itwillbs.factron.service.lot;

import com.itwillbs.factron.common.component.LotIdGenerator;
import com.itwillbs.factron.entity.Item;
import com.itwillbs.factron.repository.lot.LotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LotCreateServiceImpl {

    private final LotRepository lotRepository;
    private LotIdGenerator lotIdGenerator;

    public Void CreateInboundLot(Item item) {



        return null;
    }
}
