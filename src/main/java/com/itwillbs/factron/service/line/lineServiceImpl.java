package com.itwillbs.factron.service.line;

import com.itwillbs.factron.common.component.AuthorizationChecker;
import com.itwillbs.factron.dto.line.*;
import com.itwillbs.factron.entity.Line;
import com.itwillbs.factron.entity.Process;
import com.itwillbs.factron.mapper.line.LineMapper;
import com.itwillbs.factron.repository.process.LineRepository;
import com.itwillbs.factron.repository.process.ProcessRepository;
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
    private final ProcessRepository processRepository;

    private final AuthorizationChecker authorizationChecker;

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
     * 라인 추가 및 공정 연결
     *
     * @param requestDto 요청 DTO
     */
    @Override
    @Transactional
    public void addLine(RequestAddLineDTO requestDto) {

        // AuthorizationChecker를 사용하여 현재 로그인한 사용자 ID 가져오기
        Long empId = authorizationChecker.getCurrentEmployeeId();

        log.info("현재 로그인한 사원 ID: {}", empId);

        // 관리자 권한 체크
        checkAdminPermission(empId);

        // 라인 생성
        Line line = Line.builder()
                .name(requestDto.getLineName())
                .statusCode("LIS001") // 기본 상태 코드 (정지)
                .description(requestDto.getDescription())
                .createdBy(empId)
                .createdAt(LocalDateTime.now())
                .build();

        // 라인 저장
        Line savedLine = lineRepository.save(line);

        // 공정 리스트가 존재하면 라인에 연결
        connectProcessesToLineInternal(requestDto.getProcessIds(), savedLine);
    }

    /**
     * 라인 수정
     *
     * @param requestDto 요청 DTO
     */
    @Override
    @Transactional
    public void updateLine(RequestUpdateLineDTO requestDto) {

        // AuthorizationChecker를 사용하여 현재 로그인한 사용자 ID 가져오기
        Long empId = authorizationChecker.getCurrentEmployeeId();

        log.info("현재 로그인한 사원 ID: {}", empId);

        // 관리자 권한 체크
        checkAdminPermission(empId);

        Line line = lineRepository.findById(requestDto.getLineId())
                .orElseThrow(() -> new EntityNotFoundException("해당 라인이 존재하지 않습니다."));

        line.updateLineInfo(
                requestDto.getLineName(),
                requestDto.getDescription()
        );
    }

    /**
     * 라인에 여러 공정 한 번에 연결
     *
     * @param requestDto 요청 DTO
     */
    @Override
    @Transactional
    public void connectProcessesToLine(RequestConnectProcessesToLineDTO requestDto) {

        // AuthorizationChecker를 사용하여 현재 로그인한 사용자 ID 가져오기
        Long empId = authorizationChecker.getCurrentEmployeeId();

        log.info("현재 로그인한 사원 ID: {}", empId);

        // 관리자 권한 체크
        checkAdminPermission(empId);

        // 라인 조회
        Line line = lineRepository.findById(requestDto.getLineId())
                .orElseThrow(() -> new EntityNotFoundException("라인을 찾을 수 없습니다"));

        // 공정 리스트 처리
        connectProcessesToLineInternal(requestDto.getProcessIds(), line);
    }

    /**
     * 여러 공정을 라인에서 한 번에 연결 해제
     *
     * @param requestDto 요청 DTO
     */
    @Override
    @Transactional
    public void disconnectProcessesFromLine(RequestDisconnectProcessesFromLineDTO requestDto) {

        // AuthorizationChecker를 사용하여 현재 로그인한 사용자 ID 가져오기
        Long empId = authorizationChecker.getCurrentEmployeeId();

        log.info("현재 로그인한 사원 ID: {}", empId);

        // 관리자 권한 체크
        checkAdminPermission(empId);

        // 공정 리스트 처리
        requestDto.getProcessIds().forEach(processId -> {

            // 공정 조회
            Process process = processRepository.findById(processId)
                    .orElseThrow(() -> new EntityNotFoundException("공정 ID: " + processId + "를 찾을 수 없습니다"));

            if (process.getLine() == null) {
                throw new IllegalStateException("이미 라인과 연결 해제된 공정이 존재합니다");
            }

            // 라인 연결 해제
            process.updateLine(null);
        });
    }

    /**
     * 공정 리스트를 라인에 연결하는 내부 메소드
     *
     * @param processIds 연결할 공정 ID 목록
     * @param line 연결 대상 라인
     */
    private void connectProcessesToLineInternal(List<Long> processIds, Line line) {

        // 공정 ID 목록이 비어있거나 null인 경우 처리
        if (processIds == null || processIds.isEmpty()) {
            return;
        }

        // 공정 리스트 처리
        processIds.forEach(processId -> {
            // 공정 조회
            Process process = processRepository.findById(processId)
                    .orElseThrow(() -> new EntityNotFoundException("공정 ID: " + processId + "를 찾을 수 없습니다"));

            // hasMachine이 'Y'인 경우에만 라인 연결 가능
            if (!"Y".equals(process.getHasMachine())) {
                throw new IllegalArgumentException("설비가 없는 공정이 존재합니다");
            }

            if (process.getLine() != null) {
                throw new IllegalStateException("이미 라인에 연결되어 있는 공정이 존재합니다");
            }

            // 공정에 라인 연결
            process.updateLine(line);
        });
    }
}
