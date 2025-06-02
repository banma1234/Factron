package com.itwillbs.factron.service.sysMain;

import com.itwillbs.factron.dto.sysMain.RequestSysMain;
import com.itwillbs.factron.dto.sysMain.SysMain;
import com.itwillbs.factron.repository.syscode.SysCodeRepository;
import org.springframework.stereotype.Service;

@Service
public class SysMainService {

    private final SysCodeRepository sysCodeRepository;

    public SysMainService(SysCodeRepository sysCodeRepository) {
        this.sysCodeRepository = sysCodeRepository;
    }

    public SysMain getAllDetailByMainCode(RequestSysMain sysMain) {
        List<SysMain>
    }
}
