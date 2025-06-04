package com.itwillbs.factron.controller.sys;

import com.itwillbs.factron.dto.ResponseDTO;
import com.itwillbs.factron.dto.sys.RequestSysMainDTO;
import com.itwillbs.factron.service.sys.SysMainServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/sys/main")
@RequiredArgsConstructor
public class SysMainRestController {

    private final SysMainServiceImpl sysMainService;

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
