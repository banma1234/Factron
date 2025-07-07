package com.itwillbs.factron.service.lot;

import com.itwillbs.factron.common.component.AuthorizationChecker;
import com.itwillbs.factron.dto.lot.LotTreeDTO;
import com.itwillbs.factron.dto.lot.RequestLotUpdateDTO;
import com.itwillbs.factron.dto.lot.ResponseLotDTO;
import com.itwillbs.factron.dto.lot.ResponseLotTreeDTO;
import com.itwillbs.factron.dto.lotHistory.RequestLotHistoryDTO;
import com.itwillbs.factron.entity.Lot;
import com.itwillbs.factron.entity.WorkOrder;
import com.itwillbs.factron.entity.enums.LotType;
import com.itwillbs.factron.mapper.lot.LotMapper;
import com.itwillbs.factron.repository.lot.LotRepository;
import com.itwillbs.factron.service.Item.ItemService;
import com.itwillbs.factron.service.lotHistory.LotHistoryService;
import com.itwillbs.factron.service.material.MaterialService;
import com.itwillbs.factron.service.sys.SysDetailService;
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
    private final SysDetailService sysDetailService;
    private final ItemService itemService;
    private final MaterialService materialService;

    /**
     * 같은 조건의 LOT번호 개수 반환
     * @param map 검색 조건(id, event_type)
     * @return Long
     * */
    @Override
    public Long getLotSequence (Map<String, String> map) {
        return lotMapper.getLotSequence(map);
    }

    /**
     * LOT 이름으로 검색
     * @param lotId LOT id
     * @return responseLotDTO 반환 DTO
     * */
    @Override
    public List<ResponseLotDTO> getLotById(String lotId) {

        List<Lot> lotList;

        if(lotId == null || lotId.isEmpty()) {
            lotList = lotRepository.findByEventTypeOrderByCreatedAtDesc("ISP")
                    .orElseThrow(() -> new NoSuchElementException("해당하는 Lot 번호가 없습니다."));
        } else {
            lotList = lotRepository
                    .findByIdContaining(lotId)
                    .orElseThrow(() -> new NoSuchElementException("해당하는 Lot 번호가 없습니다."));
        }

        return toLotDTOList(lotList);
    }

    /**
     * 대상 LOT 트리 검색
     * @param lotId LOT id
     * @return responseLotTreeDTO 반환 DTO
     * */
    @Override
    public ResponseLotTreeDTO getLotTreeById(String lotId) {

        List<LotTreeDTO> lotList  = lotMapper.getDetailLotTreeById(lotId);

        // 트리구조로 변환 후 return
        return convertLotToTree(lotList, lotId);
    }

    /**
     * 출고시 입력받은 모든 품목/자재 quantity 업데이트
     * @param materialList 자재 List
     * @return Void
     * */
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

        // 입력받은 품목/자재 종류별로 순환하며 로직 순회  ex) 합판(5), 베어링(2), ...
        materialList.forEach(target -> {

            List<Lot> LotList = new ArrayList<>();
            List<Lot> allLots = lotMapper.getInboundLotById(target);

            Long sum = 0L;
            // 품목/자재 종류별로 순회하며 선입선출을 기준으로 업데이트 대상 식별.
            for (Lot lot : allLots) {
                // 더이상 출고해도 되지 않을 경우 break
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

            // 품목/자재 구분해서 업데이트
            String key = (target.getMaterial_id() == null || target.getMaterial_id().isEmpty())
                    ? target.getItem_id()
                    : target.getMaterial_id();

            List<Lot> lots = map.getOrDefault(key, Collections.emptyList());

            // 업데이트 수행
            updateLotsAndRecordHistory(lots, targetQuantity, target.getWork_order_id());
        });

        return null;
    }

    /**
     * 실제 quantity 업데이트 메서드
     * @param lots LOT List
     * @param targetQuantity 업데이트 수량
     * @param workOrderId 작업지시 id
     * */
    private void updateLotsAndRecordHistory(List<Lot> lots, Long targetQuantity, WorkOrder workOrderId) {

        for (Lot lot : lots) {
            Long originalQuantity = lot.getQuantity();

            if (originalQuantity == 0) {
                continue;
            }

            Long updatedQuantity = originalQuantity - targetQuantity;
            lot.updateQuantity(Math.max(0, updatedQuantity));

            // 대상 LOT 업데이트
            lotMapper.updateLotQuantity(lot);
            // LOT_HISTORY에 업데이트 사항 기록
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

    /**
     * List 단위로 Entity => DTO 변환
     * @param lotList Lot List
     * @return ResponseLotDTO 반환 DTO
     * */
    private List<ResponseLotDTO> toLotDTOList(List<Lot> lotList) {

        return lotList.stream()
                .map(this::fromEntity)
                .toList();
    }

    /**
     * 공통코드 등 포맷에 맞게 Entity => DTO 변환
     * @param entity Entity
     * @return responseLotDTO
     * */
    private ResponseLotDTO fromEntity(Lot entity) {
        return ResponseLotDTO.builder()
                .id(entity.getId())
                .item_id(entity.getItem() != null
                        ? itemService.getItemByCode(entity.getItem().getId())
                        : null)
                .material_id(entity.getMaterial() != null
                        ? itemService.getItemByCode(entity.getMaterial().getId())
                        : null)
                .quantity(entity.getQuantity())
                .event_type(entity.getEventType().matches("^PTP\\d{3}$")
                        ? sysDetailService.getDetailBySysCode(entity.getEventType())
                        : LotType.getDescriptionByPrefix(entity.getEventType()))
                .created_by(entity.getCreatedBy())
                .created_at(entity.getCreatedAt())
                .build();
    }

    /**
     * 실제 데이터 트리구조로 변환하는 메서드
     * @param dtoList DTO List
     * @param rootId 최상위 node id
     * @return ResponseLotTreeDTO 반환 DTO
     * */
    private ResponseLotTreeDTO convertLotToTree(List<LotTreeDTO> dtoList, String rootId) {
        Map<String, ResponseLotTreeDTO> nodeMap  = new HashMap<>();

        for (LotTreeDTO node : dtoList) {
            ResponseLotTreeDTO DTO = convertLotToResponse(node);
            nodeMap .put(DTO.getId(), DTO);
        }

        for (LotTreeDTO node : dtoList) {
            String parentId = node.getParentId();

            // 부모 자식 매핑
            if (parentId != null && nodeMap.containsKey(parentId)) {

                ResponseLotTreeDTO parent = nodeMap.get(parentId);
                ResponseLotTreeDTO child = nodeMap.get(node.getId());

                parent.getChildren().add(child);
            }
        }

        return nodeMap.get(rootId);
    }

    /**
     * 공통코드 등 포맷에 맞게 DTO 변환
     * @param dto LotTreeDTO 변환 전 DTO
     * @return ResponseLotTreeDTO 변환 후 DTO
     * */
    private ResponseLotTreeDTO convertLotToResponse(LotTreeDTO dto) {
        return ResponseLotTreeDTO.builder()
                .id(dto.getId())
                .itemId(dto.getItemId() != null
                        ? itemService.getItemByCode(dto.getItemId())
                        : null)
                .materialId(dto.getMaterialId() != null
                        ? materialService.getMaterialByCode(dto.getMaterialId())
                        : null)
                .quantity(dto.getQuantity())
                .eventType(dto.getEventType().matches("^PTP\\d{3}$")
                        ? sysDetailService.getDetailBySysCode(dto.getEventType())
                        : LotType.getDescriptionByPrefix(dto.getEventType()))
                .parentId(dto.getParentId())
                .createdBy(dto.getCreatedBy())
                .createdAt(dto.getCreatedAt())
                .children(new ArrayList<>())
                .build();
    }

}
