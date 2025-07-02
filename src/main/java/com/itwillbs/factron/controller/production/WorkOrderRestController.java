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
     * 투입할 품목 목록 조회
     * */
    @GetMapping("/inputs")
    public ResponseDTO<List<ResponseWorkProdDTO>> getPossibleInputList(RequestWorkProdDTO requestWorkProdDTO) {
        try {
            return ResponseDTO.success(workOrderService.getPossibleInputList(requestWorkProdDTO));
        } catch (Exception e) {
            return ResponseDTO.fail(800, "투입 품목 목록 조회에 실패했습니다.", workOrderService.getPossibleInputList(requestWorkProdDTO));
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

    /*
     * 투입된 품목 및 작업자 목록 조회
     * */
    @GetMapping("/dtl")
    public ResponseDTO<ResponseWorkDetailDTO> getWorkOrderDetail(String orderId) {
        try {
            return ResponseDTO.success(workOrderService.getWorkOrderDetail(orderId));
        } catch (Exception e) {
            return ResponseDTO.fail(800, "작업 상세정보 조회에 실패했습니다.", workOrderService.getWorkOrderDetail(orderId));
        }
    }

    /*
     * 작업지시 시작
     * */
    @PutMapping()
    public ResponseDTO<Void> startWorkOrder(@RequestBody RequestWorkOrderDTO requestWorkOrderDTO) {
        try {
            return ResponseDTO.success("작업이 시작되었습니다!<br/>공정을 완료해주세요.", workOrderService.startWorkOrder(requestWorkOrderDTO));
        } catch (NoSuchElementException nse) {
            return ResponseDTO.fail(800, nse.getMessage(), null);
        } catch (IllegalArgumentException iae) {
            return ResponseDTO.fail(801, iae.getMessage(), null);
        } catch (Exception e) {
            return ResponseDTO.fail(802, "작업 시작에 실패했습니다.", null);
        }
    }
}
