package com.itwillbs.factron.service.sys;

import com.itwillbs.factron.dto.sys.RequestSysMainDTO;
import com.itwillbs.factron.dto.sys.SysMainDTO;
import com.itwillbs.factron.entity.SysCode;
import com.itwillbs.factron.repository.syscode.SysCodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
//@Transactional(readOnly = true)
public class SysMainServiceImpl implements SysMainService {

    private final SysCodeRepository sysCodeRepository;

    @Override
//    @Transactional
    public Void saveSysMain(RequestSysMainDTO requestSysMainDTO) {

        log.info(">>>>>>>>>>>>>>>>>>>변환 전 : {} ", requestSysMainDTO.toString());
        log.info(">>>>>>>>>>>>>>>>>>>저장할놈 : {} ", toMainEntity(requestSysMainDTO).toString());

        sysCodeRepository.save(toMainEntity(requestSysMainDTO));

        return null;
    }

    private SysCode toMainEntity(RequestSysMainDTO requestSysMainDTO) {

        return RequestSysMainDTO.toEntity(requestSysMainDTO);
    }
}
