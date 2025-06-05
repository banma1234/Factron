package com.itwillbs.factron.service.work;

import com.itwillbs.factron.dto.work.RequestWorkDTO;
import com.itwillbs.factron.dto.work.ResponseWorkDTO;

import java.util.List;

public interface WorkService {
    List<ResponseWorkDTO> getWorkList(RequestWorkDTO requestWorkDTO);

    Void registWork(RequestWorkDTO requestWorkDTO);
}
