package com.itwillbs.factron.service.process;

import com.itwillbs.factron.common.component.AuthorizationChecker;
import com.itwillbs.factron.dto.process.RequestAddProcessDTO;
import com.itwillbs.factron.dto.process.RequestProcessInfoDTO;
import com.itwillbs.factron.dto.process.RequestUpdateProcessDTO;
import com.itwillbs.factron.dto.process.ResponseProcessInfoDTO;
import com.itwillbs.factron.entity.Process;
import com.itwillbs.factron.mapper.process.ProcessMapper;
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
public class ProcessServiceImpl implements ProcessService {

    private final ProcessRepository processRepository;
    private final ProcessMapper processMapper;

    private final AuthorizationChecker authorizationChecker;

    /**
     * 공정 추가
     *
     * @param requestDto 요청 DTO
     */
    @Override
    @Transactional
    public void addProcess(RequestAddProcessDTO requestDto) {

        // AuthorizationChecker를 사용하여 현재 로그인한 사용자 ID 가져오기
        Long empId = authorizationChecker.getCurrentEmployeeId();

        log.info("현재 로그인한 사원 ID: {}", empId);

        // 관리자 권한 체크
        authorizationChecker.checkAnyAuthority("ATH003", "ATH007");

        Process process = Process.builder()
                .name(requestDto.getProcessName())
                .description(requestDto.getDescription())
                .typeCode(requestDto.getProcessTypeCode())
                .standardTime(requestDto.getStandardTime())
                .hasMachine("N")
                .createdBy(empId)
                .createdAt(LocalDateTime.now())
                .build();

        processRepository.save(process);
    }

    /**
     * 공정 수정
     *
     * @param requestDto   요청 DTO
     */
    @Override
    @Transactional
    public void updateProcess(RequestUpdateProcessDTO requestDto) {

        // 관리자 권한 체크
        authorizationChecker.checkAnyAuthority("ATH003", "ATH007");

        Process process = processRepository.findById(requestDto.getProcessId())
                .orElseThrow(() -> new EntityNotFoundException("해당 공정을 찾을 수 없습니다."));

        process.updateProcessInfo(
                requestDto.getProcessName(),
                requestDto.getDescription(),
                requestDto.getProcessTypeCode(),
                requestDto.getStandardTime()
        );
    }

    /**
     * 공정 목록 조회
     *
     * @param requestDto 요청 DTO
     * @return 공정 목록
     */
    @Override
    public List<ResponseProcessInfoDTO> getProcessList(RequestProcessInfoDTO requestDto) {

        return processMapper.selectProcessList(requestDto);
    }
}
