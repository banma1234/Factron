package com.itwillbs.factron.service.Item;

import com.itwillbs.factron.dto.item.RequestitemDTO;
import com.itwillbs.factron.dto.item.ResponseItemDTO;

import java.util.List;

public interface ItemService {
    List<ResponseItemDTO> getItemList(RequestitemDTO dto);
    Void addItem(List<RequestitemDTO> dtolist);
    Void updateItem(List<RequestitemDTO> dtoList);
}