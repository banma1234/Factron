package com.itwillbs.factron.service.line;

import com.itwillbs.factron.dto.line.RequestAddLineDTO;
import com.itwillbs.factron.dto.line.RequestLineInfoDTO;
import com.itwillbs.factron.dto.line.ResponseLineInfoDTO;
import com.itwillbs.factron.entity.Line;
import com.itwillbs.factron.mapper.line.LineMapper;
import com.itwillbs.factron.repository.process.LineRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class lineServiceImpl implements lineService {

    private final LineRepository lineRepository;
    private final LineMapper lineMapper;

    /**
     * 라인 목록 조회
     *
     * @param requestDto 요청 DTO
     * @return 라인 목록
     */
    @Override
    public List<ResponseLineInfoDTO> getLineList(RequestLineInfoDTO requestDto) {

        return lineMapper.selectLineList(requestDto);
    }

    /**
     * 라인 추가
     *
     * @param requestDto 요청 DTO
     */
    @Override
    @Transactional
    public void addLine(RequestAddLineDTO requestDto, Long empId) {

        Line line = Line.builder()
                .name(requestDto.getLineName())
                .statusCode("LIN001") // 라인 첫 생성 시 상태 코드 정지로 고정
                .description(requestDto.getDescription())
                .createdBy(empId)
                .createdAt(LocalDateTime.now())
                .build();

        lineRepository.save(line);
    }
}
