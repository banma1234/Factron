package com.itwillbs.factron.service.lotHistory;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LotHistoryServiceImpl {

   @Transactional
   public Void updateInboundLotHistory() {

   }
}
