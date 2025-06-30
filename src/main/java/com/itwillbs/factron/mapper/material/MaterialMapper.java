package com.itwillbs.factron.mapper.material;

import com.itwillbs.factron.dto.material.MaterialRequestDTO;
import com.itwillbs.factron.dto.material.MaterialResponseDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MaterialMapper {
    List<MaterialResponseDTO> getMaterialList(MaterialRequestDTO dto);
}
