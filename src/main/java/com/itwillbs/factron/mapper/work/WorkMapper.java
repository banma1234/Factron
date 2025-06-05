package com.itwillbs.factron.mapper.work;

import com.itwillbs.factron.dto.work.RequestWorkDTO;
import com.itwillbs.factron.dto.work.ResponseWorkDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface WorkMapper {

    List<ResponseWorkDTO> getWorkList(RequestWorkDTO requestWorkDTO);
}
