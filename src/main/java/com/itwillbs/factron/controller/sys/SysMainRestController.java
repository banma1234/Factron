package com.itwillbs.factron.controller.sys;

import com.itwillbs.factron.dto.ResponseDTO;
import com.itwillbs.factron.dto.sys.RequestSysMainDTO;
import com.itwillbs.factron.dto.sys.ResponseSysMainDTO;
import com.itwillbs.factron.service.sys.SysMainServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/sys/main")
@RequiredArgsConstructor
public class SysMainRestController {

    private final SysMainServiceImpl sysMainService;

    /**
     * sysCode 검색 및 조회
     * @param mainCode 메인코드
     * @return ResponseDTO
     * */
    @GetMapping("")
    public ResponseDTO<List<ResponseSysMainDTO>> getSysMain(
            @RequestParam(required = false) String mainCode
    ) {

        try {

            List<ResponseSysMainDTO> mainList = sysMainService.getMainSysCode(mainCode);

            return ResponseDTO.success(mainList);
        } catch (Exception e) {

            return ResponseDTO.fail(
                    800,
                    e.getMessage(),
                    null
            );
        }
    }

    /**
     * sysCode 삽입
     * @param requestSysMainDTO requestMainDTO 요청 DTO
     * @return ResponseDTO
     * */
    @PostMapping("")
    public ResponseDTO<Void> saveSysMain(@Valid @RequestBody RequestSysMainDTO requestSysMainDTO) {

        try {
            return ResponseDTO.success(
                    "메인코드 입력이 완료되었습니다.",
                    sysMainService.saveSysMain(requestSysMainDTO)
            );
        } catch (Exception e) {

            return ResponseDTO.fail(
                    800,
                    e.getMessage(),
                    null
            );
        }
    }

    /**
     * sysCode 수정
     * @param requestSysMainDTO requestMainDTO 요청 DTO
     * @return ResponseDTO
     * */
    @PutMapping("")
    public ResponseDTO<Void> updateSysMain(@Valid @RequestBody RequestSysMainDTO requestSysMainDTO) {

        try {
            return ResponseDTO.success(
                    "메인코드 수정이 완료되었습니다.",
                    sysMainService.updateSysMain(requestSysMainDTO)
            );
        } catch (Exception e) {

            return ResponseDTO.fail(
                    800,
                    e.getMessage(),
                    null
            );
        }
    }
}
