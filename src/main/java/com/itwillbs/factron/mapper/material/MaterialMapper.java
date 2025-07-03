package com.itwillbs.factron.mapper.material;

import com.itwillbs.factron.dto.material.RequestMaterialDTO;
import com.itwillbs.factron.dto.material.ResponseMaterialDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MaterialMapper {
    List<ResponseMaterialDTO> getMaterialList(RequestMaterialDTO dto);
}
