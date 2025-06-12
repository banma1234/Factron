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

    @GetMapping("")
    public ResponseDTO<List<ResponseSysDetailDTO>> getDetailById(@Valid @RequestParam Long id) {

        try {
            List<ResponseSysDetailDTO> detailList = sysDetailService
                    .getAllDetailByMainCode(id);

            return ResponseDTO.success(detailList);
        } catch (Exception e) {
            return ResponseDTO.fail(
                    800,
                    "존재하지 않는 메인코드입니다.",
                    sysDetailService.getAllDetailByMainCode(id)
            );
        }
    }

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
