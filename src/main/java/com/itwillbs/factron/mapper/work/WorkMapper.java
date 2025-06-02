package com.itwillbs.factron.mapper.work;

import com.itwillbs.factron.dto.work.RequestWork;
import com.itwillbs.factron.dto.work.ResponseWork;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface WorkMapper {

    List<ResponseWork> getWorkList(RequestWork requestWork);
}
