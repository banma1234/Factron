package com.itwillbs.factron.mapper.item;

import com.itwillbs.factron.dto.item.ItemRequestDTO;
import com.itwillbs.factron.dto.item.ItemResponseDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ItemMapper {
    List<ItemResponseDTO> getItemList(ItemRequestDTO dto);
}