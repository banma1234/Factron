package com.itwillbs.factron.service.purchase;

import com.itwillbs.factron.dto.purchase.RequestSearchPurchaseDTO;
import com.itwillbs.factron.dto.purchase.ResponseSearchPurchaseDTO;
import com.itwillbs.factron.mapper.purchase.PurchaseMapper;
import com.itwillbs.factron.repository.purchase.PurchaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PurchaseServiceImpl implements PurchaseServcie{
    private final PurchaseRepository purchaseRepository;
    private final PurchaseMapper purchaseMapper;

    @Override
    public List<ResponseSearchPurchaseDTO> getPurchaseList(RequestSearchPurchaseDTO requestSearchPurchaseDTO){
        return purchaseMapper.getPurchaseList(requestSearchPurchaseDTO);
    }
}
