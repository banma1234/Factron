package com.itwillbs.factron.mapper.work;

import com.itwillbs.factron.dto.work.RequestWorkDTO;
import com.itwillbs.factron.dto.work.ResponseWorkDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface WorkMapper {

    /*
     * 근무 목록 조회
     * */
    List<ResponseWorkDTO> getWorkList(RequestWorkDTO requestWorkDTO);

    /*
     * 중복 근무 체크
     * */
    List<ResponseWorkDTO> chkDuplicateWork(RequestWorkDTO requestWorkDTO);
}
