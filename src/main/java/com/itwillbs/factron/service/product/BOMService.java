package com.itwillbs.factron.service.product;

import com.itwillbs.factron.dto.product.RequestBOMDTO;
import com.itwillbs.factron.dto.product.ResponseBOMDTO;

import java.util.List;

public interface BOMService {
    List<ResponseBOMDTO> getBOMList(RequestBOMDTO requestBOMDTO);
}
