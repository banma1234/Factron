package com.itwillbs.factron.controller.item;

import com.itwillbs.factron.dto.ResponseDTO;
import com.itwillbs.factron.dto.item.ItemRequestDTO;
import com.itwillbs.factron.dto.item.ItemResponseDTO;
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

    @GetMapping
    public ResponseDTO<List<ItemResponseDTO>> getItemList(ItemRequestDTO dto) {
        try {
            return ResponseDTO.success(itemService.getItemList(dto));
        } catch (Exception e) {
            return ResponseDTO.fail(800, "제품 목록 조회에 실패했습니다.", itemService.getItemList(dto));
        }
    }

    @PostMapping
    public ResponseDTO<Void> addItem(@RequestBody ItemRequestDTO dto) {
        try {
            return ResponseDTO.success("제품 등록이 완료되었습니다!", itemService.addItem(dto));
        } catch (NoSuchElementException nse) {
            return ResponseDTO.fail(800, nse.getMessage(), null);
        } catch (Exception e) {
            return ResponseDTO.fail(801, "제품 등록에 실패했습니다.", null);
        }
    }

    @PutMapping
    public ResponseDTO<Void> updateItem(@RequestBody ItemRequestDTO dto) {
        try {
            return ResponseDTO.success("제품 수정이 완료되었습니다!", itemService.updateItem(dto));
        } catch (NoSuchElementException nse) {
            return ResponseDTO.fail(800, nse.getMessage(), null);
        } catch (Exception e) {
            return ResponseDTO.fail(801, "제품 수정에 실패했습니다.", null);
        }
    }
}