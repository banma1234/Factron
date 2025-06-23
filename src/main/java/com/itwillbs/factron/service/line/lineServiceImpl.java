package com.itwillbs.factron.service.line;

import com.itwillbs.factron.dto.line.RequestAddLineDTO;
import com.itwillbs.factron.dto.line.RequestLineInfoDTO;
import com.itwillbs.factron.dto.line.RequestUpdateLineDTO;
import com.itwillbs.factron.dto.line.ResponseLineInfoDTO;
import com.itwillbs.factron.entity.Line;
import com.itwillbs.factron.mapper.line.LineMapper;
import com.itwillbs.factron.repository.process.LineRepository;
import jakarta.persistence.EntityNotFoundException;
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
     * 관리자 권한 체크
     *
     * @param empId 사원 ID
     */
    private void checkAdminPermission(Long empId) {

        boolean hasPermission = true; // TODO: 실제 권한 체크 로직으로 대체

        // 관리자 권한이 없는 경우 예외 처리
        if (!hasPermission) {
            throw new SecurityException("관리자 권한이 없습니다.");
        }
    }

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
     * @param empId      사원 ID
     */
    @Override
    @Transactional
    public void addLine(RequestAddLineDTO requestDto, Long empId) {

        // 관리자 권한 체크
        checkAdminPermission(empId);

        Line line = Line.builder()
                .name(requestDto.getLineName())
                .statusCode("LIS001") // 기본 상태 코드 (정지)
                .description(requestDto.getDescription())
                .createdBy(empId)
                .createdAt(LocalDateTime.now())
                .build();

        lineRepository.save(line);
    }

    /**
     * 라인 수정
     *
     * @param requestDto 요청 DTO
     * @param empId      사원 ID
     */
    @Override
    @Transactional
    public void updateLine(RequestUpdateLineDTO requestDto, Long empId) {

        // 관리자 권한 체크
        checkAdminPermission(empId);

        Line line = lineRepository.findById(requestDto.getLineId())
                .orElseThrow(() -> new EntityNotFoundException("해당 라인이 존재하지 않습니다."));

        line.updateLineInfo(
                requestDto.getLineName(),
                requestDto.getDescription()
        );
    }
}
