package com.itwillbs.factron.service.sys;

import com.itwillbs.factron.dto.sys.RequestSysMainDTO;
import com.itwillbs.factron.dto.sys.ResponseSysMainDTO;
import com.itwillbs.factron.dto.sys.SysMainDTO;

import java.util.List;

public interface SysMainService {

    Void saveSysMain(RequestSysMainDTO requestSysMainDTO);

    List<ResponseSysMainDTO> getMainSysCode(String mainCode);
}
