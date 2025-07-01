package com.itwillbs.factron.service.Item;

import com.itwillbs.factron.dto.item.ItemRequestDTO;
import com.itwillbs.factron.dto.item.ItemResponseDTO;

import java.util.List;

public interface ItemService {
    List<ItemResponseDTO> getItemList(ItemRequestDTO dto);
    Void addItem(ItemRequestDTO dto);
    Void updateItem(ItemRequestDTO dto);
}