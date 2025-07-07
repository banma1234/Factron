package com.itwillbs.factron.controller.storage;

import com.itwillbs.factron.dto.ResponseDTO;
import com.itwillbs.factron.dto.storage.ResponseStorageDTO;
import com.itwillbs.factron.service.storage.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/storage")
@RequiredArgsConstructor
public class StorageRestController {

    private final StorageService storageService;

    @GetMapping("")
    public ResponseDTO<List<ResponseStorageDTO>> getStorage(
            @RequestParam(required = false) String name
    ) {

        try {
            List<ResponseStorageDTO> storageList = storageService.getStorageByName(name);

            return ResponseDTO.success(storageList);
        } catch (Exception e) {
            return ResponseDTO.fail(
                    800,
                    "존재하지 않는 창고입니다.",
                    null // ❗ 절대 service 다시 호출 금지
            );
        }
    }
}
