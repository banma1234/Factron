package com.itwillbs.factron.service.work;

import com.itwillbs.factron.dto.work.RequestWork;
import com.itwillbs.factron.dto.work.ResponseWork;

import java.util.List;

public interface WorkService {
    List<ResponseWork> getWorkList(RequestWork requestWork);
}
