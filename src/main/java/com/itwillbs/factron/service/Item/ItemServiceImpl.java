package com.itwillbs.factron.service.Item;

import com.itwillbs.factron.dto.item.ItemRequestDTO;
import com.itwillbs.factron.dto.item.ItemResponseDTO;
import com.itwillbs.factron.entity.Item;
import com.itwillbs.factron.mapper.item.ItemMapper;
import com.itwillbs.factron.repository.product.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
@Log4j2
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemMapper itemMapper;
    private final ItemRepository itemRepository;

    @Override
    public List<ItemResponseDTO> getItemList(ItemRequestDTO dto) {
        return itemMapper.getItemList(dto);
    }

    @Override
    public Void addItem(ItemRequestDTO dto) {
        if (itemRepository.findById(dto.getItemId()).isPresent()) {
            throw new IllegalArgumentException("이미 등록된 ID 입니다");
        }

        Item item = Item.builder()
                .id(dto.getItemId())
                .name(dto.getName())
                .unit(dto.getUnit())
                .price(dto.getPrice())
                .typeCode(dto.getTypeCode())
                .createdBy(dto.getCreatedBy())
                .build();

        itemRepository.save(item);
        return null;
    }

    @Transactional
    @Override
    public Void updateItem(ItemRequestDTO dto) {
        Item item = itemRepository.findById(dto.getItemId())
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 제품입니다."));

        item.updateItem(dto);
        return null;
    }
}