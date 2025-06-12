package com.itwillbs.factron.controller.sys;

import com.itwillbs.factron.dto.ResponseDTO;
import com.itwillbs.factron.dto.sys.RequestSysDetailDTO;
import com.itwillbs.factron.dto.sys.ResponseSysDetailDTO;
import com.itwillbs.factron.service.sys.SysDetailServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/sys/detail")
@RequiredArgsConstructor
public class SysDetailRestController {

    private final SysDetailServiceImpl sysDetailService;

    /**
     * detailSysCode 검색 및 조회
     * @param mainCode 메인코드
     * @param name 이름(필수X)
     * @return responseDetailDTO 반환 DTO
     * */
    @GetMapping("")
    public ResponseDTO<List<ResponseSysDetailDTO>> getDetailById(
            @Valid @RequestParam String mainCode,
            @RequestParam(required = false) String name
    ) {

        try {
            List<ResponseSysDetailDTO> detailList = sysDetailService
                    .getDetailByParams(mainCode, name);

            return ResponseDTO.success(detailList);
        } catch (Exception e) {
            return ResponseDTO.fail(
                    800,
                    "존재하지 않는 메인코드입니다.",
                    sysDetailService.getDetailByParams(mainCode, name)
            );
        }
    }

    /**
     * detailSysCode 삽입
     * @param requestSysDetailDTO requestDetailDTO 요청 DTO
     * @return ResponseDTO
     * */
    @PostMapping("")
    public ResponseDTO<Void> saveSysDetail(@Valid @RequestBody RequestSysDetailDTO requestSysDetailDTO) {

        try {
            return ResponseDTO.success(sysDetailService.saveSysDetail(requestSysDetailDTO));
        } catch (Exception e) {
            return ResponseDTO.fail(
                    800,
                    "존재하지 않는 메인코드입니다.",
                    sysDetailService.saveSysDetail(requestSysDetailDTO)
            );
        }
    }

    /**
     * detailSysCode 수정
     * @param requestSysDetailDTO requestDetailDTO 요청 DTO
     * @return ResponseDTO
     * */
    @PutMapping("")
    public ResponseDTO<Void> updateSysDetail(@Valid @RequestBody RequestSysDetailDTO requestSysDetailDTO) {

        try {
            return ResponseDTO.success(sysDetailService.updateSysDetail(requestSysDetailDTO));
        } catch (Exception e) {
            return ResponseDTO.fail(
                    800,
                    "잘못된 입력입니다.",
                    sysDetailService.updateSysDetail(requestSysDetailDTO)
            );
        }
    }

}
