package com.itwillbs.factron.controller.material;

import com.itwillbs.factron.dto.ResponseDTO;
import com.itwillbs.factron.dto.material.RequestMaterialDTO;
import com.itwillbs.factron.dto.material.ResponseMaterialDTO;
import com.itwillbs.factron.service.material.MaterialService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/material")
@RequiredArgsConstructor
@Log4j2
public class MaterialRestController {

    private final MaterialService materialService;

    @GetMapping
    public ResponseDTO<List<ResponseMaterialDTO>> getMaterialList(RequestMaterialDTO dto) {
        try {
            return ResponseDTO.success(materialService.getMaterialList(dto));
        } catch (Exception e) {
            return ResponseDTO.fail(800, "자재 목록 조회에 실패했습니다.", materialService.getMaterialList(dto));
        }
    }



    @PostMapping
    public ResponseDTO<Void> addMaterial(@RequestBody RequestMaterialDTO dto) {
        try {
            return ResponseDTO.success("자재 등록이 완료되었습니다!",  materialService.addMaterial(dto));
        } catch (NoSuchElementException nse) {
            return ResponseDTO.fail(800, nse.getMessage(), null);
        } catch (Exception e) {
            return ResponseDTO.fail(801, "자재 등록에 실패했습니다.", null);
        }
    }

    @PutMapping
    public ResponseDTO<Void> updateMaterial(@RequestBody RequestMaterialDTO dto) {
        try {
            return ResponseDTO.success("자재 정보 수정이 완료되었습니다!",  materialService.updateMaterial(dto));
        } catch (NoSuchElementException nse) {
            return ResponseDTO.fail(800, nse.getMessage(), null);
        } catch (Exception e) {
            return ResponseDTO.fail(801, "자재 정보 수정에 실패했습니다.", null);
        }
    }
}

