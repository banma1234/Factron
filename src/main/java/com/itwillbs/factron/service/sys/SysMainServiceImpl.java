package com.itwillbs.factron.service.sys;

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
    public Void saveSysMain(SysMainDTO sysMainDTO) {

        sysCodeRepository.save(toMainEntity(sysMainDTO));

        return null;
    }

    private SysCode toMainEntity(SysMainDTO sysMainDTO) {

        return SysMainDTO.toEntity(sysMainDTO);
    }
}
