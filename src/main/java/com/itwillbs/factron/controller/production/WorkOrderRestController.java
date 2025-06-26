package com.itwillbs.factron.controller.production;

import com.itwillbs.factron.dto.ResponseDTO;
import com.itwillbs.factron.dto.production.RequestWorkOrderDTO;
import com.itwillbs.factron.dto.production.ResponseWorkOrderDTO;
import com.itwillbs.factron.service.production.WorkOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/workorder")
public class WorkOrderRestController {

    private final WorkOrderService workOrderService;

    /*
    * 작업지시 목록 조회
    * */
    @GetMapping()
    public ResponseDTO<List<ResponseWorkOrderDTO>> getWorkOrderList(RequestWorkOrderDTO requestWorkOrderDTO) {
        try {
            return ResponseDTO.success(workOrderService.getWorkOrderList(requestWorkOrderDTO));
        } catch (Exception e) {
            return ResponseDTO.fail(800, "작업지시 목록 조회에 실패했습니다.", workOrderService.getWorkOrderList(requestWorkOrderDTO));
        }
    }

    /*
     * 작업지시 등록
     * */
    @PostMapping()
    public ResponseDTO<Void> registWorkOrder(@RequestBody RequestWorkOrderDTO requestWorkOrderDTO) {
        try {
            return ResponseDTO.success("작업지시 등록이 완료되었습니다!", workOrderService.registWorkOrder(requestWorkOrderDTO));
        } catch (NoSuchElementException nse) {
            return ResponseDTO.fail(800, nse.getMessage(), null);
        } catch (Exception e) {
            return ResponseDTO.fail(801, "작업지시 등록에 실패했습니다.", null);
        }
    }
}
