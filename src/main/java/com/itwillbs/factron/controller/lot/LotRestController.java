package com.itwillbs.factron.controller.lot;

import com.itwillbs.factron.dto.ResponseDTO;
import com.itwillbs.factron.dto.lot.ResponseLotDTO;
import com.itwillbs.factron.service.lot.LotService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/lot")
@RequiredArgsConstructor
public class LotRestController {

    private final LotService lotService;

    @GetMapping("")
    public ResponseDTO<List<ResponseLotDTO>> getLots(
            @RequestParam(required = false) String lotId
    ) {
        try {
            List<ResponseLotDTO> lotList = lotService.getLotById(lotId);

            return ResponseDTO.success(lotList);
        } catch (Exception e) {
            return ResponseDTO.fail(
                    800,
                    "찾을 수 없습니다.",
                    null
            );
        }
    }

}
