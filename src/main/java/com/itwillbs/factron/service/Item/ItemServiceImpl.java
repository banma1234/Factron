package com.itwillbs.factron.service.Item;

import com.itwillbs.factron.dto.item.RequestitemDTO;
import com.itwillbs.factron.dto.item.ResponseItemDTO;
import com.itwillbs.factron.entity.Item;
import com.itwillbs.factron.mapper.item.ItemMapper;
import com.itwillbs.factron.repository.product.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemMapper itemMapper;
    private final ItemRepository itemRepository;


    /*
     * ITEM 목록 조회
     * */
    @Override
    public List<ResponseItemDTO> getItemList(RequestitemDTO dto) {
        return itemMapper.getItemList(dto);
    }


    /*
     * ITEM 등록
     * */
    @Override
    public Void addItem(List<RequestitemDTO> dtoList) {

        for (RequestitemDTO dto : dtoList) {
            // ID 중복 체크 (선택)
            if (itemRepository.findById(dto.getItemId()).isPresent()) {
                throw new IllegalArgumentException("이미 등록된 ID 입니다: " + dto.getItemId());
            }

            // Entity 변환
            Item item = Item.builder()
                    .id(dto.getItemId())
                    .name(dto.getName())
                    .unit(dto.getUnit())
                    .price(dto.getPrice())
                    .typeCode(dto.getTypeCode())
                    .createdBy(dto.getCreatedBy())
                    .build();

            itemRepository.save(item);
        }
        return null;
    }

    /*
     * ITEM 수정
     * */
    @Transactional
    @Override
    public Void updateItem(List<RequestitemDTO> dtoList) {
        for (RequestitemDTO dto : dtoList) {
            Item item = itemRepository.findById(dto.getItemId())
                    .orElseThrow(() -> new NoSuchElementException("존재하지 않는 ID 입니다: " + dto.getItemId()));

            item.updateItem(dto);
        }

        return null;
    }
}