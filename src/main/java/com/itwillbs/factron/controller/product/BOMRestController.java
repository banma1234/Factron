package com.itwillbs.factron.controller.product;

import com.itwillbs.factron.dto.ResponseDTO;
import com.itwillbs.factron.dto.product.RequestBOMDTO;
import com.itwillbs.factron.dto.product.ResponseBOMDTO;
import com.itwillbs.factron.service.product.BOMService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bom")
public class BOMRestController {

    private final BOMService bomService;

    /*
    * BOM 목록 조회
    * */
    @GetMapping()
    public ResponseDTO<List<ResponseBOMDTO>> getBOMList(RequestBOMDTO requestBOMDTO) {
        try {
            return ResponseDTO.success(bomService.getBOMList(requestBOMDTO));
        } catch (Exception e) {
            return ResponseDTO.fail(800, "BOM 목록 조회에 실패했습니다.", bomService.getBOMList(requestBOMDTO));
        }
    }
}
