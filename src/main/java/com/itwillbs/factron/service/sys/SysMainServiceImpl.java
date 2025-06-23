package com.itwillbs.factron.service.sys;

import com.itwillbs.factron.common.component.AuthorizationChecker;
import com.itwillbs.factron.dto.sys.RequestSysMainDTO;
import com.itwillbs.factron.dto.sys.ResponseSysDetailDTO;
import com.itwillbs.factron.dto.sys.ResponseSysMainDTO;
import com.itwillbs.factron.entity.DetailSysCode;
import com.itwillbs.factron.entity.SysCode;
import com.itwillbs.factron.repository.syscode.SysCodeRepository;
import jakarta.transaction.SystemException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SysMainServiceImpl implements SysMainService {

    private final SysCodeRepository sysCodeRepository;

    /**
     * 공통코드 추가
     * @param requestSysMainDTO 요청 DTO
     * @return Void
     * */
    @Override
    @Transactional
    public Void saveSysMain(RequestSysMainDTO requestSysMainDTO) {

        sysCodeRepository.save(RequestSysMainDTO.toEntity(requestSysMainDTO));

        return null;
    }

    /**
     * 공통코드 목록 조회
     * @param mainCode 메인코드 - 필수 아님
     * @return responseMainDTO 반환 DTO
     * */
    @Override
    public List<ResponseSysMainDTO> getMainSysCode(String mainCode) {

        List<SysCode> sysCodeList;

        if (mainCode == null || mainCode.isEmpty()) {
            sysCodeList = sysCodeRepository.findAll();
        } else {
            sysCodeList = sysCodeRepository.findByMainCodeContaining(mainCode)
                    .orElseThrow(() -> new NoSuchElementException("존재하지 않는 상세코드입니다."));
        }

        return toMainDTOList(sysCodeList);
    }

    /**
     * 공통코드 수정
     * @param requestSysMainDTO 요청 DTO
     * @return Void
     * */
    @Transactional
    @Override
    public Void updateSysMain(@Valid RequestSysMainDTO requestSysMainDTO) {

        SysCode sysCode = sysCodeRepository
                .findByMainCode(requestSysMainDTO.getMain_code())
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 상세코드입니다."))
                .getFirst();

        sysCode.updateSysCode(requestSysMainDTO);

        return null;
    }

    /**
     * Entity List -> DTO 변환
     * @param sysCodeList 엔티티
     * @return responseMainDTO 반환 DTO
     * */
    private List<ResponseSysMainDTO> toMainDTOList(List<SysCode> sysCodeList) {

        return sysCodeList.stream()
                .map(ResponseSysMainDTO::fromEntity)
                .toList();

    }

}
