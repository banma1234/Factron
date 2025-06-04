package com.itwillbs.factron.service.sys;

import com.itwillbs.factron.dto.sys.RequestSysMainDTO;
import com.itwillbs.factron.dto.sys.SysMainDTO;
import com.itwillbs.factron.entity.SysCode;
import com.itwillbs.factron.repository.syscode.SysCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SysMainServiceImpl implements SysMainService {

    private final SysCodeRepository sysCodeRepository;

    @Override
    @Transactional
    public Void saveSysMain(RequestSysMainDTO requestSysMainDTO) {

        sysCodeRepository.save(toMainEntity(requestSysMainDTO));

        return null;
    }

    private SysCode toMainEntity(RequestSysMainDTO requestSysMainDTO) {

        return RequestSysMainDTO.toEntity(requestSysMainDTO);
    }
}
