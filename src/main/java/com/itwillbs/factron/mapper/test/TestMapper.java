package com.itwillbs.factron.mapper.test;

import com.itwillbs.factron.dto.test.RequestTest;
import com.itwillbs.factron.dto.test.Test;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TestMapper {

    List<Test> getTestList(RequestTest srhTest);
}
