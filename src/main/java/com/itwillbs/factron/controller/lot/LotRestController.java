package com.itwillbs.factron.controller.lot;

import com.itwillbs.factron.dto.ResponseDTO;
import com.itwillbs.factron.dto.lot.ResponseLotDTO;
import com.itwillbs.factron.dto.lot.ResponseLotTreeDTO;
import com.itwillbs.factron.service.lot.LotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/lot")
@RequiredArgsConstructor
public class LotRestController {

    private final LotService lotService;

    /**
     * LOT 이름으로 검색
     * @param lotId LOT id
     * @return ResponseDTO 반환 DTO
     * */
    @GetMapping("/search")
    public ResponseDTO<List<ResponseLotDTO>> searchLotById(
            @RequestParam(required = false) String lotId
    ) {
        try {
            List<ResponseLotDTO> lotList = lotService.getLotById(lotId);

            return ResponseDTO.success(lotList);
        } catch (Exception e) {

            log.error(e.getMessage());

            return ResponseDTO.fail(
                    800,
                    "찾을 수 없습니다.",
                    null
            );
        }
    }

    @GetMapping("")
    public ResponseDTO<ResponseLotTreeDTO> getLotsTree(
            @RequestParam String lotId
    ) {
        try {
            ResponseLotTreeDTO lotTree = lotService.getLotTreeById(lotId);

            return ResponseDTO.success(lotTree);
        } catch (Exception e) {

            log.error(e.getMessage());

            return ResponseDTO.fail(
                    800,
                    "찾을 수 없습니다.",
                    null
            );
        }
    }

}
