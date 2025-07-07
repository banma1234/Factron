package com.itwillbs.factron.service.material;

import com.itwillbs.factron.dto.material.RequestMaterialDTO;
import com.itwillbs.factron.dto.material.ResponseMaterialDTO;

import java.util.List;

public interface MaterialService {
    List<ResponseMaterialDTO> getMaterialList(RequestMaterialDTO dto);
    Void addMaterial(RequestMaterialDTO dto);
    Void updateMaterial(RequestMaterialDTO dto);
    String getMaterialByCode(String code);
}