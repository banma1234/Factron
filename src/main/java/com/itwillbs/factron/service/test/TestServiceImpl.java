package com.itwillbs.factron.service.test;

import com.itwillbs.factron.dto.test.RequestTest;
import com.itwillbs.factron.dto.test.Test;
import com.itwillbs.factron.mapper.test.TestMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

//    private final TestRepository testRepository;
    private final TestMapper testMapper;

    /*
    * 테스트 목록 조회
    */
    @Override
    public List<Test> getTestList(RequestTest srhTest) {
//        return testMapper.getTestList(srhTest); // 원래 리턴 방식

//        테스트 데이터 임시 생성
        List<Test> testList = new ArrayList<>();
        for(int i=1; i<=10; i++) {
            Test test = Test.builder()
                    .id((long)i)
                    .name("name")
                    .chkType(i/2==0)
                    .birth(LocalDate.now())
                    .address("address")
                    .filePath("filePath")
                    .regDate(LocalDateTime.now())
                    .build();
            testList.add(test);
        }

        return testList;
    }
}
