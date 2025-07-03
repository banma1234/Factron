package com.itwillbs.factron.controller.item;

import com.itwillbs.factron.dto.ResponseDTO;
import com.itwillbs.factron.dto.item.RequestitemDTO;
import com.itwillbs.factron.dto.item.ResponseItemDTO;
import com.itwillbs.factron.service.Item.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;


@RestController
@RequestMapping("/api/item")
@RequiredArgsConstructor
@Log4j2
public class ItemRestController {

    private final ItemService itemService;

    /*
     * ITEM 목록 조회
     * */
    @GetMapping
    public ResponseDTO<List<ResponseItemDTO>> getItemList(RequestitemDTO dto) {
        try {
            return ResponseDTO.success(itemService.getItemList(dto));
        } catch (Exception e) {
            return ResponseDTO.fail(800, "제품 목록 조회에 실패했습니다.", itemService.getItemList(dto));
        }
    }

    /*
     * ITEM 등록
     * */
    @PostMapping
    public ResponseDTO<Void> addItem(@RequestBody List<RequestitemDTO> dtoList) {
        try {
            return ResponseDTO.success("제품 등록이 완료되었습니다!", itemService.addItem(dtoList));
        } catch (NoSuchElementException nse) {
            return ResponseDTO.fail(800, nse.getMessage(), null);
        } catch (Exception e) {
            return ResponseDTO.fail(801, "제품 등록에 실패했습니다.", null);
        }
    }

    /*
     * ITEM 수정
     * */
    @PutMapping
    public ResponseDTO<Void> updateItem(@RequestBody List<RequestitemDTO> dtoList) {
        try {
            return ResponseDTO.success("제품 수정이 완료되었습니다!", itemService.updateItem(dtoList));
        } catch (NoSuchElementException nse) {
            return ResponseDTO.fail(800, nse.getMessage(), null);
        } catch (Exception e) {
            return ResponseDTO.fail(801, "제품 수정에 실패했습니다.", null);
        }
    }
}