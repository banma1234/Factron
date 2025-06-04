package com.itwillbs.factron.service.sys;

import com.itwillbs.factron.dto.sys.SysDetailDTO;

import java.util.List;

public interface SysDetailService {

    List<SysDetailDTO> getAllDetailByMainCode(Long id);
}
