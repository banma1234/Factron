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
@RequestMapping("/sys/main")
@RequiredArgsConstructor
public class SysMainRestController {

    private final SysMainServiceImpl sysMainService;

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
                    "조회할 수 없습니다.",
                    sysMainService.getMainSysCode(mainCode)
            );
        }
    }

    @PostMapping("")
    public ResponseDTO<Void> saveSysMain(@Valid @RequestBody RequestSysMainDTO requestSysMainDTO) {
        try {
            return ResponseDTO.success(sysMainService.saveSysMain(requestSysMainDTO));
        } catch (Exception e) {
            return ResponseDTO.fail(
                    800,
                    "잘못된 입력입니다.",
                    sysMainService.saveSysMain(requestSysMainDTO)
            );
        }
    }

}
