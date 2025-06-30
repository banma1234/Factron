package com.itwillbs.factron.controller.production;

import com.itwillbs.factron.dto.ResponseDTO;
import com.itwillbs.factron.dto.production.*;
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
     * 작업지시 내릴 수 있는 제품 목록 조회
     * */
    @GetMapping("/items")
    public ResponseDTO<List<ResponseWorkProdDTO>> getWorkItemList(RequestWorkProdDTO requestWorkProdDTO) {
        try {
            return ResponseDTO.success(workOrderService.getWorkItemList(requestWorkProdDTO));
        } catch (Exception e) {
            return ResponseDTO.fail(800, "작업 제품 목록 조회에 실패했습니다.", workOrderService.getWorkItemList(requestWorkProdDTO));
        }
    }

    /*
     * 작업 제품 정보 및 투입 품목 목록 조회
     * */
    @GetMapping("/inputs")
    public ResponseDTO<List<ResponseWorkProdDTO>> getInputProdList(RequestWorkProdDTO requestWorkProdDTO) {
        try {
            return ResponseDTO.success(workOrderService.getInputProdList(requestWorkProdDTO));
        } catch (Exception e) {
            return ResponseDTO.fail(800, "투입 품목 목록 조회에 실패했습니다.", workOrderService.getInputProdList(requestWorkProdDTO));
        }
    }

    /*
     * 작업 가능한 사원 목록 조회
     * */
    @GetMapping("/worker")
    public ResponseDTO<List<ResponseWorkerDTO>> getPossibleWorkerList() {
        try {
            return ResponseDTO.success(workOrderService.getPossibleWorkerList());
        } catch (Exception e) {
            return ResponseDTO.fail(800, "작업자 목록 조회에 실패했습니다.", workOrderService.getPossibleWorkerList());
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
        } catch (IllegalArgumentException iae) {
            return ResponseDTO.fail(801, iae.getMessage(), null);
        } catch (Exception e) {
            return ResponseDTO.fail(802, "작업지시 등록에 실패했습니다.", null);
        }
    }
}
