package com.itwillbs.factron.service.sys;

import com.itwillbs.factron.dto.sys.SysDetailDTO;
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

    @Override
    public List<SysDetailDTO> getAllDetailByMainCode(Long id) {

        List<DetailSysCode> details = detailSysCodeRepository.findBySysCode_Id(id);

        return toDetailDTOList(details);
    }

    private List<SysDetailDTO> toDetailDTOList(List<DetailSysCode> details) {

        return details.stream()
                .map(SysDetailDTO :: fromEntity )
                .toList();
    }
}
