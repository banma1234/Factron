package com.itwillbs.factron.controller.material;

import com.itwillbs.factron.dto.ResponseDTO;
import com.itwillbs.factron.dto.material.MaterialRequestDTO;
import com.itwillbs.factron.dto.material.MaterialResponseDTO;
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
    public ResponseDTO<List<MaterialResponseDTO>> getMaterialList(MaterialRequestDTO dto) {
        try {
            return ResponseDTO.success(materialService.getMaterialList(dto));
        } catch (Exception e) {
            return ResponseDTO.fail(800, "자재 목록 조회에 실패했습니다.", materialService.getMaterialList(dto));
        }
    }



    @PostMapping
    public ResponseDTO<Void> addMaterial(@RequestBody MaterialRequestDTO dto) {
        try {
            return ResponseDTO.success("BOM 등록이 완료되었습니다!",  materialService.addMaterial(dto));
        } catch (NoSuchElementException nse) {
            return ResponseDTO.fail(800, nse.getMessage(), null);
        } catch (Exception e) {
            return ResponseDTO.fail(801, "BOM 등록에 실패했습니다.", null);
        }
    }

    @PutMapping
    public ResponseDTO<Void> updateMaterial(@RequestBody MaterialRequestDTO dto) {
        try {
            return ResponseDTO.success("BOM 수정이 완료되었습니다!",  materialService.updateMaterial(dto));
        } catch (NoSuchElementException nse) {
            return ResponseDTO.fail(800, nse.getMessage(), null);
        } catch (Exception e) {
            return ResponseDTO.fail(801, "BOM 수정에 실패했습니다.", null);
        }
    }
}

