package com.itwillbs.factron.service.sys;

import com.itwillbs.factron.dto.sys.SysDetailDTO;
import com.itwillbs.factron.entity.DetailSysCode;
import com.itwillbs.factron.repository.syscode.DetailSysCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SysDetailService {

    private final DetailSysCodeRepository detailSysCodeRepository;

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
