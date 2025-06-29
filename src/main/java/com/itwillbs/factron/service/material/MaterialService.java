package com.itwillbs.factron.service.material;

import com.itwillbs.factron.dto.material.MaterialRequestDTO;
import com.itwillbs.factron.dto.material.MaterialResponseDTO;

import java.util.List;

public interface MaterialService {
    List<MaterialResponseDTO> getMaterialList(MaterialRequestDTO dto);
    Void addMaterial(MaterialRequestDTO dto);
    Void updateMaterial(MaterialRequestDTO dto);
}