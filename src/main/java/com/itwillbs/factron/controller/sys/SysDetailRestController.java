package com.itwillbs.factron.controller.sys;

import com.itwillbs.factron.dto.ResponseDTO;
import com.itwillbs.factron.dto.sys.SysDetailDTO;
import com.itwillbs.factron.service.sys.SysDetailServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/sys/detail")
@RequiredArgsConstructor
public class SysDetailRestController {

    private final SysDetailServiceImpl sysDetailService;

    @GetMapping("")
    public ResponseDTO<List<SysDetailDTO>> getDetailById(@RequestParam Long id) {

        try {
            List<SysDetailDTO> detailList = sysDetailService.getAllDetailByMainCode(id);

            return ResponseDTO.success(detailList);
        } catch (Exception e) {
            return ResponseDTO.fail(
                    800,
                    "존재하지 않는 메인코드입니다.",
                    sysDetailService.getAllDetailByMainCode(id)
            );
        }
    }

}
