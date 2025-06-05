package com.itwillbs.factron.service.sys;

import com.itwillbs.factron.dto.sys.RequestSysDetailDTO;
import com.itwillbs.factron.dto.sys.ResponseSysDetailDTO;
import com.itwillbs.factron.entity.DetailSysCode;
import com.itwillbs.factron.entity.SysCode;
import com.itwillbs.factron.repository.syscode.DetailSysCodeRepository;
import com.itwillbs.factron.repository.syscode.SysCodeRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SysDetailServiceImpl implements SysDetailService {

    private final DetailSysCodeRepository detailSysCodeRepository;
    private final SysCodeRepository sysCodeRepository;

    // Main.id = Detail.sys_code_id 인 데이터 모두 조회
    @Override
    public List<ResponseSysDetailDTO> getAllDetailByMainCode(Long id) {

        List<DetailSysCode> details = detailSysCodeRepository
                .findBySysCode_Id(id);

        return toDetailDTOList(details);
    }

    // 추가하려는 DetailCode의 부모인 MainCode 조회 후 해당 엔티티 객체를 전달, DetailCode 저장 수행
    @Transactional
    @Override
    public Void saveSysDetail(@Valid RequestSysDetailDTO requestSysDetailDTO) {

        String mainCode = requestSysDetailDTO.getMain_code();
        SysCode parentSysCode = sysCodeRepository.findByMainCode(mainCode);

        DetailSysCode detailSysCode = toDetailEntity(requestSysDetailDTO, parentSysCode);

        detailSysCodeRepository.save(detailSysCode);

        return null;
    }

    // Entity List -> DTO 변환
    private List<ResponseSysDetailDTO> toDetailDTOList(List<DetailSysCode> details) {

        return details.stream()
                .map(ResponseSysDetailDTO:: fromEntity )
                .toList();
    }

    private DetailSysCode toDetailEntity(RequestSysDetailDTO DTO, SysCode sysCode) {

        return RequestSysDetailDTO.toEntity(DTO, sysCode);
    }

}
