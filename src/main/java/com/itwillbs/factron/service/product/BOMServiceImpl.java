package com.itwillbs.factron.service.product;

import com.itwillbs.factron.dto.product.RequestBOMDTO;
import com.itwillbs.factron.dto.product.ResponseBOMDTO;
import com.itwillbs.factron.entity.Bom;
import com.itwillbs.factron.entity.Item;
import com.itwillbs.factron.entity.Material;
import com.itwillbs.factron.mapper.product.BOMMapper;
import com.itwillbs.factron.repository.product.BomRepository;
import com.itwillbs.factron.repository.product.ItemRepository;
import com.itwillbs.factron.repository.product.MaterialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BOMServiceImpl implements BOMService {

    private final BOMMapper bomMapper;
    private final BomRepository bomRepository;
    private final ItemRepository itemRepository;
    private final MaterialRepository materialRepository;

    /*
     * BOM 목록 조회
     * */
    @Override
    public List<ResponseBOMDTO> getBOMList(RequestBOMDTO requestBOMDTO) {
        return bomMapper.getBOMList(requestBOMDTO);
    }

    /*
     * BOM 등록 가능한 품목 목록 조회
     * */
    @Override
    public List<ResponseBOMDTO> getPossibleProdList(RequestBOMDTO requestBOMDTO) {
        return bomMapper.getPossibleProdList(requestBOMDTO);
    }

    /*
     * BOM 등록
     * */
    @Transactional
    @Override
    public Void registBOM(RequestBOMDTO requestBOMDTO) {
        Item parentItem = itemRepository.findById(requestBOMDTO.getParentItemId()).orElseThrow(() -> new NoSuchElementException("존재하지 않는 품목입니다."));
        Item childItem = null;
        Material childMaterial = null;

        if (requestBOMDTO.getChildItemId() != null && !requestBOMDTO.getChildItemId().isEmpty()) {
            childItem = itemRepository.findById(requestBOMDTO.getChildItemId()).orElseThrow(() -> new NoSuchElementException("존재하지 않는 품목입니다."));
        } else {
            childMaterial = materialRepository.findById(requestBOMDTO.getChildMaterialId()).orElseThrow(() -> new NoSuchElementException("존재하지 않는 품목입니다."));
        }

        // bom 등록
        bomRepository.save(Bom.builder()
                .parentItem(parentItem)
                .childItem(childItem)
                .childMaterial(childMaterial)
                .consumption(requestBOMDTO.getConsumption())
                .build());

        return null;
    }

    /*
     * BOM 수정
     * */
    @Transactional
    @Override
    public Void updateBOM(RequestBOMDTO requestBOMDTO) {
        Bom bom = bomRepository.findById(requestBOMDTO.getId()).orElseThrow(() -> new NoSuchElementException("존재하지 않는 BOM입니다."));
        bom.changeConsumption(requestBOMDTO.getConsumption()); // 소요량만 수정
        return null;
    }

    /*
     * BOM 삭제
     * */
    @Transactional
    @Override
    public Void deleteBOM(RequestBOMDTO requestBOMDTO) {
        // 재귀 쿼리로 하위 BOM 모두 찾아서 삭제
        List<Long> delIds = bomMapper.findDelBomList(requestBOMDTO);
        if (delIds != null && !delIds.isEmpty()) {
            bomMapper.deleteBOM(delIds);
        }
        return null;
    }
}
