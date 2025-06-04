package com.itwillbs.factron.service.sys;

import com.itwillbs.factron.dto.sys.RequestSysMainDTO;
import com.itwillbs.factron.dto.sys.ResponseSysDetailDTO;
import com.itwillbs.factron.entity.DetailSysCode;
import com.itwillbs.factron.repository.syscode.DetailSysCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SysDetailServiceImpl implements SysDetailService {

    private final DetailSysCodeRepository detailSysCodeRepository;

    // Main.id = Detail.sys_code_id 인 데이터 모두 조회
    @Override
    public List<ResponseSysDetailDTO> getAllDetailByMainCode(Long id) {

        List<DetailSysCode> details = detailSysCodeRepository
                .findBySysCode_Id(id);

        return toDetailDTOList(details);
    }

    // Entity -> DTO 변환
    private List<ResponseSysDetailDTO> toDetailDTOList(List<DetailSysCode> details) {

        return details.stream()
                .map(ResponseSysDetailDTO:: fromEntity )
                .toList();
    }
}
