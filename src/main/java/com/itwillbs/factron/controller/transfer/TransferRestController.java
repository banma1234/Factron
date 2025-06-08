package com.itwillbs.factron.controller.transfer;

import com.itwillbs.factron.dto.ResponseDTO;
import com.itwillbs.factron.dto.transfer.RequestTransferDTO;
import com.itwillbs.factron.dto.transfer.ResponseTransferDTO;
import com.itwillbs.factron.service.transfer.TransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/trans")
public class TransferRestController {

    private final TransferService transferService;

    /**
     * 인사발령 목록 조회
     */
    @GetMapping()
    public ResponseDTO<List<ResponseTransferDTO>> getTransferList(RequestTransferDTO requestTransferDTO) {

        try {

            return ResponseDTO.success(transferService.getTransferList(requestTransferDTO));
        } catch (Exception e) {

            return ResponseDTO.fail(800, "인사발령 목록 조회에 실패했습니다.", transferService.getTransferList(requestTransferDTO));
        }
    }

    /**
     * 인사발령 등록 (결재 추가)
     *
     */
    @PostMapping()
    public ResponseDTO<Void> registTransfer(@RequestBody RequestTransferDTO requestTransferDTO) {

        try {
            return ResponseDTO.success("인사발령 등록 결재 신청이 완료되었습니다!", transferService.registTransfer(requestTransferDTO));
        } catch (NoSuchElementException nse) {

            return ResponseDTO.fail(800, nse.getMessage(), null);
        } catch (IllegalStateException ise) {

            return ResponseDTO.fail(801, ise.getMessage(), null);
        } catch (Exception e) {

            return ResponseDTO.fail(802, "인사발령 등록에 실패했습니다.", null);
        }
    }
}
