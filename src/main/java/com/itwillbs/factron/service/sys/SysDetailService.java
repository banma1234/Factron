package com.itwillbs.factron.service.sys;

import com.itwillbs.factron.dto.sys.RequestSysMainDTO;
import com.itwillbs.factron.dto.sys.ResponseSysDetailDTO;
import com.itwillbs.factron.dto.sys.SysDetailDTO;

import java.util.List;

public interface SysDetailService {

    List<ResponseSysDetailDTO> getAllDetailByMainCode(Long id);
}
