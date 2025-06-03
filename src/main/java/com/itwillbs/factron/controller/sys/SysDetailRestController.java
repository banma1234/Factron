package com.itwillbs.factron.controller.sys;

import com.itwillbs.factron.dto.ResponseDTO;
import com.itwillbs.factron.dto.sys.RequestSysDetail;
import com.itwillbs.factron.dto.sys.SysDetail;
import com.itwillbs.factron.service.sys.SysDetailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/sys")
@RequiredArgsConstructor
public class SysDetailRestController {

    private final SysDetailService sysDetailService;

    @GetMapping("/detail")
    public ResponseDTO<SysDetail> getDetailById(@RequestParam Long id) {

        try {
            return ResponseDTO.success(sysDetailService.getAllDetailByMainCode(id));
        } catch (Exception e) {
            return ResponseDTO.fail(
                    800,
                    "존재하지 않는 메인코드입니다.",
                    sysDetailService.getAllDetailByMainCode(id)
            );
        }
    }

}
