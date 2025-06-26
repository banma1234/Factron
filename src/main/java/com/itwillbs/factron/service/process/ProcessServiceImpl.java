package com.itwillbs.factron.service.process;

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
     * 공정 추가
     *
     * @param requestDto 요청 DTO
     * @param empId      사원 ID
     */
    @Override
    @Transactional
    public void addProcess(RequestAddProcessDTO requestDto, Long empId) {

        checkAdminPermission(empId); // 관리자 권한 체크

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
     * @param dto   요청 DTO
     * @param empId 사원 ID
     */
    @Override
    @Transactional
    public void updateProcess(RequestUpdateProcessDTO dto, Long empId) {

        checkAdminPermission(empId); // 관리자 권한 체크

        Process process = processRepository.findById(dto.getProcessId())
                .orElseThrow(() -> new EntityNotFoundException("해당 공정을 찾을 수 없습니다."));

        process.updateProcessInfo(
                dto.getProcessName(),
                dto.getDescription(),
                dto.getProcessTypeCode(),
                dto.getStandardTime()
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
