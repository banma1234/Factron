package com.itwillbs.factron.mapper.item;

import com.itwillbs.factron.dto.item.RequestitemDTO;
import com.itwillbs.factron.dto.item.ResponseItemDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ItemMapper {
    List<ResponseItemDTO> getItemList(RequestitemDTO dto);
}