package com.itwillbs.factron.service.outbound;

import com.itwillbs.factron.dto.outbound.RequestOutboundCompleteDTO;
import com.itwillbs.factron.dto.outbound.RequestSearchOutboundDTO;
import com.itwillbs.factron.dto.outbound.ResponseSearchOutboundDTO;
import com.itwillbs.factron.entity.*;
import com.itwillbs.factron.mapper.outbound.OutboundMapper;
import com.itwillbs.factron.repository.contract.ContractRepository;
import com.itwillbs.factron.repository.lot.LotRepository;
import com.itwillbs.factron.repository.storage.OutboundRepository;
import com.itwillbs.factron.repository.storage.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OutboundServiceImpl implements OutboundService {

    private final OutboundMapper outboundMapper;
    private final OutboundRepository outboundRepository;
    private final ContractRepository contractRepository;
    private final StockRepository stockRepository;
    private final LotRepository lotRepository;

    @Override
    public List<ResponseSearchOutboundDTO> getOutboundsList(RequestSearchOutboundDTO requestSearchOutboundDTO) {
        return outboundMapper.getOutboundsList(requestSearchOutboundDTO);
    }

    @Override
    @Transactional
    public void updateOutbound(RequestOutboundCompleteDTO dto) {
        for (Long outboundId : dto.getOutboundIds()) {
            Outbound outbound = outboundRepository.findById(outboundId)
                    .orElseThrow(() -> new IllegalArgumentException("출고 데이터를 찾을 수 없습니다: " + outboundId));

            // 1. 출고 상태 + 출고일자 업데이트
            outbound.updateStatus();

            Long quantityToSubtract = outbound.getQuantity();

            // 2. 재고에서 차감
            if (outbound.getItem() != null) {
                Stock stock = stockRepository.findByItemAndStorage(outbound.getItem(), outbound.getStorage())
                        .orElseThrow(() -> new IllegalStateException("재고를 찾을 수 없습니다 (제품): " + outbound.getItem().getId()));
                stock.subtractQuantity(quantityToSubtract);
            } else if (outbound.getMaterial() != null) {
                Stock stock = stockRepository.findByMaterialAndStorage(outbound.getMaterial(), outbound.getStorage())
                        .orElseThrow(() -> new IllegalStateException("재고를 찾을 수 없습니다 (자재): " + outbound.getMaterial().getId()));
                stock.subtractQuantity(quantityToSubtract);
            } else {
                throw new IllegalStateException("출고 데이터에 제품/자재 정보가 없습니다: " + outboundId);
            }

            // 3. LOT 테이블에서 FIFO 방식으로 출고량만큼 차감
            if (outbound.getItem() != null) {
                subtractLotFIFO(outbound.getItem(), null, quantityToSubtract);
            } else if (outbound.getMaterial() != null) {
                subtractLotFIFO(null, outbound.getMaterial(), quantityToSubtract);
            }

            // 4. 출고 완료 여부 확인 후 Contract 상태 변경
            if (outbound.getContract() != null) {
                Long contractId = outbound.getContract().getId();
                boolean existsNotCompleted = outboundRepository.existsByContractIdAndStatusCodeNot(contractId, "STS003");
                if (!existsNotCompleted) {
                    Contract contract = contractRepository.findById(contractId)
                            .orElseThrow(() -> new IllegalArgumentException("수주 데이터를 찾을 수 없습니다: " + contractId));
                    contract.updateStatus("STP004"); // 예: 수주 출고 완료 상태 코드
                }
            }
        }
    }

    /**
     * LOT에서 FIFO로 출고량만큼 차감
     */
    private void subtractLotFIFO(Item item, Material material, Long quantityToSubtract) {
        List<Lot> lots;
        if (item != null) {
            lots = lotRepository.findByItemAndQuantityGreaterThanOrderByCreatedAtAsc(item, 0L);
        } else if (material != null) {
            lots = lotRepository.findByMaterialAndQuantityGreaterThanOrderByCreatedAtAsc(material, 0L);
        } else {
            throw new IllegalArgumentException("LOT 차감 시 Item/Material 둘 다 null 입니다");
        }

        Long remaining = quantityToSubtract;

        for (Lot lot : lots) {
            if (remaining <= 0) break;
            Long subtracted = lot.subtractAsMuchAsPossible(remaining);
            remaining -= subtracted;
        }

        if (remaining > 0) {
            throw new IllegalStateException("LOT 수량이 부족하여 출고할 수 없습니다. 부족 수량: " + remaining);
        }
    }
}
