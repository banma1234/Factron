package com.itwillbs.factron.service.sys;

import com.itwillbs.factron.dto.sys.SysDetail;
import com.itwillbs.factron.dto.sys.SysMain;
import com.itwillbs.factron.repository.syscode.SysCodeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysMainService {

    private final SysCodeRepository sysCodeRepository;

    public SysMainService(SysCodeRepository sysCodeRepository) {
        this.sysCodeRepository = sysCodeRepository;
    }
}
