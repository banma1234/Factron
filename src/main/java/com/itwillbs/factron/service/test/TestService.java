package com.itwillbs.factron.service.test;

import com.itwillbs.factron.dto.test.RequestTest;
import com.itwillbs.factron.dto.test.Test;

import java.util.List;

public interface TestService {

    List<Test> getTestList(RequestTest srhTest);
}
