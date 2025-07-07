package com.itwillbs.factron.controller.inbound;

import com.itwillbs.factron.dto.ResponseDTO;
import com.itwillbs.factron.dto.inbound.RequestInboundCompleteDTO;
import com.itwillbs.factron.dto.inbound.RequestSearchInboundDTO;
import com.itwillbs.factron.dto.inbound.ResponseSearchInboundDTO;
import com.itwillbs.factron.service.inbound.InboundService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/inbound")
@RequiredArgsConstructor
public class InboundRestController {
    private final InboundService inboundService;

    // 입고 전체 조회
    @GetMapping("")
    public ResponseDTO<List<ResponseSearchInboundDTO>> getInboundsList(RequestSearchInboundDTO requestSearchInboundDTO) {
        try {
            return ResponseDTO.success(inboundService.getInboundsList(requestSearchInboundDTO));
        }catch (Exception e){
            return ResponseDTO.fail(800,"조회된 결과가 없습니다.",inboundService.getInboundsList(requestSearchInboundDTO));
        }
    }

    // 입고 처리
    @PutMapping("")
    public ResponseDTO<Void> updateInbound(@RequestBody RequestInboundCompleteDTO  requestInboundCompleteDTO) {
        try{
            inboundService.updateInbound(requestInboundCompleteDTO);
            return ResponseDTO.success("입고에 성공했습니다.",null);
        }catch (Exception e){
            return ResponseDTO.fail(800,"입고에 실패 했습니다.",null);
        }
    }
}
