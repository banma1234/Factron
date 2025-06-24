package com.itwillbs.factron.controller.product;

import com.itwillbs.factron.dto.ResponseDTO;
import com.itwillbs.factron.dto.product.RequestBOMDTO;
import com.itwillbs.factron.dto.product.ResponseBOMDTO;
import com.itwillbs.factron.service.product.BOMService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

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

    /*
     * BOM 등록
     * */
    @PostMapping()
    public ResponseDTO<Void> registBOM(@RequestBody RequestBOMDTO requestBOMDTO) {
        try {
            return ResponseDTO.success("BOM 등록이 완료되었습니다!", bomService.registBOM(requestBOMDTO));
        } catch (NoSuchElementException nse) {
            return ResponseDTO.fail(800, nse.getMessage(), null);
        } catch (Exception e) {
            return ResponseDTO.fail(801, "BOM 등록에 실패했습니다.", null);
        }
    }

    /*
     * BOM 수정
     * */
    @PutMapping()
    public ResponseDTO<Void> updateBOM(@RequestBody RequestBOMDTO requestBOMDTO) {
        try {
            return ResponseDTO.success("BOM 수정이 완료되었습니다!", bomService.updateBOM(requestBOMDTO));
        } catch (NoSuchElementException nse) {
            return ResponseDTO.fail(800, nse.getMessage(), null);
        } catch (Exception e) {
            return ResponseDTO.fail(801, "BOM 수정에 실패했습니다.", null);
        }
    }

    /*
     * BOM 삭제
     * */
    @DeleteMapping()
    public ResponseDTO<Void> deleteBOM(@RequestBody RequestBOMDTO requestBOMDTO) {

        try {
            return ResponseDTO.success("BOM 삭제가 완료되었습니다!", bomService.deleteBOM(requestBOMDTO));
        } catch (Exception e) {
            return ResponseDTO.fail(800, "BOM 삭제에 실패했습니다.", null);
        }
    }
}
