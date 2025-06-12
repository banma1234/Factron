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
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SysDetailServiceImpl implements SysDetailService {

    private final DetailSysCodeRepository detailSysCodeRepository;
    private final SysCodeRepository sysCodeRepository;

    /**
     * detailSysCode 목록호출
     * @param mainCode 상세공통코드 ID
     * @return responseDetailDTO 반환 DTO
     * */
    @Override
    public List<ResponseSysDetailDTO> getAllDetailByMainCode(String mainCode) {

        List<DetailSysCode> details = detailSysCodeRepository
                .findByMainCode(mainCode)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 상세코드입니다."));

        return toDetailDTOList(details);
    }

    /**
     * detailSysCode 파라미터로 검색
     * @param mainCode
     * @return responseDetailDTO 반환 DTO
     * */
    public List<ResponseSysDetailDTO> getDetailByParams(@Valid String mainCode, String name) {

        List<DetailSysCode> details;

        if(name == null || name.isEmpty()) {
            details = detailSysCodeRepository
                    .findByMainCode(mainCode)
                    .orElseThrow(() -> new NoSuchElementException("존재하지 않는 상세코드입니다."));
        } else {
            details = detailSysCodeRepository
                    .findByMainCodeAndNameContaining(mainCode, name)
                    .orElseThrow(() -> new NoSuchElementException("존재하지 않는 상세코드입니다."));
        }

        return toDetailDTOList(details);
    }

    /**
    * 상세공통코드 저장
    * @param requestSysDetailDTO 요청 DTO
    * @return Void
    * */
    @Transactional
    @Override
    public Void saveSysDetail(@Valid RequestSysDetailDTO requestSysDetailDTO) {

        String mainCode = requestSysDetailDTO.getMain_code();
        List<SysCode> parentSysCode = sysCodeRepository.findByMainCode(mainCode)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 상세코드입니다."));

        // mainCode는 unique이고 해당하는 컬럼은 하나밖에 없기 때문에 이렇게 꺼냈다.
        DetailSysCode detailSysCode = toDetailEntity(requestSysDetailDTO, parentSysCode.getFirst());

        detailSysCodeRepository.save(detailSysCode);

        return null;
    }

    /**
    * 상세공통코드 수정
    * @param requestSysDetailDTO 요청 DTO
    * @return Void
    * */
    @Transactional
    @Override
    public Void updateSysDetail(@Valid RequestSysDetailDTO requestSysDetailDTO) {

        DetailSysCode detailSysCode = detailSysCodeRepository
                .findByDetailCode(requestSysDetailDTO.getDetail_code())
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 상세코드입니다.."));

        detailSysCode.updateSysCode(requestSysDetailDTO);

        return null;
    }

    /**
    * Entity List -> DTO 변환
    * @param details 엔티티
    * @return responseDetailDTO 반환 DTO
    * */
    private List<ResponseSysDetailDTO> toDetailDTOList(List<DetailSysCode> details) {

        return details.stream()
                .map(ResponseSysDetailDTO :: fromEntity )
                .toList();
    }

    private DetailSysCode toDetailEntity(RequestSysDetailDTO DTO, SysCode sysCode) {

        return RequestSysDetailDTO.toEntity(DTO, sysCode);
    }

}
