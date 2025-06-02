package com.itwillbs.factron.controller.syscode;

import com.itwillbs.factron.dto.ResponseDTO;
import com.itwillbs.factron.dto.sys.RequestSysMain;
import com.itwillbs.factron.dto.sys.SysMain;
import com.itwillbs.factron.service.sys.SysMainService;
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
public class SysRestController {

    // 클릭해서 조회하는건 SysDetailService에서 조회해야됨

//    private final SysMainService sysMainService;
//
//    @GetMapping("/main")
//    public ResponseDTO<SysMain> getSysDetails(@RequestParam RequestSysMain sysMain) {
//        try {
//            Long id = sysMain.getId();
//
//            return ResponseDTO.success(sysMainService.getAllDetailByMainCode(id));
//        } catch (Exception e) {
//
//            return ResponseDTO.fail(
//                    800,
//                    "존재하지 않는 메인코드입니다.",
//                    sysMainService.getAllDetailByMainCode(sysMain)
//            );
//        }
//    }
}
