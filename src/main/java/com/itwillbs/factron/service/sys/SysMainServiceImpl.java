package com.itwillbs.factron.service.sys;

import com.itwillbs.factron.dto.sys.RequestSysMainDTO;
import com.itwillbs.factron.dto.sys.ResponseSysDetailDTO;
import com.itwillbs.factron.dto.sys.ResponseSysMainDTO;
import com.itwillbs.factron.entity.DetailSysCode;
import com.itwillbs.factron.entity.SysCode;
import com.itwillbs.factron.repository.syscode.SysCodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SysMainServiceImpl implements SysMainService {

    private final SysCodeRepository sysCodeRepository;

    @Override
    @Transactional
    public Void saveSysMain(RequestSysMainDTO requestSysMainDTO) {

        sysCodeRepository.save(RequestSysMainDTO.toEntity(requestSysMainDTO));

        return null;
    }

    @Override
    public List<ResponseSysMainDTO> getMainSysCode(String mainCode) {

        List<SysCode> sysCodeList;

        if (mainCode == null || mainCode.isEmpty()) {
            sysCodeList = sysCodeRepository.findAll();
        } else {
            sysCodeList = sysCodeRepository.findByMainCode(mainCode);
        }

        return toMainDTOList(sysCodeList);
    }

    // Entity List -> DTO 변환
    private List<ResponseSysMainDTO> toMainDTOList(List<SysCode> sysCodeList) {

        return sysCodeList.stream()
                .map(ResponseSysMainDTO::fromEntity)
                .toList();

    }

}
