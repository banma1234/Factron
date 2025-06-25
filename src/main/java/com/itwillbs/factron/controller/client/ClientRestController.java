package com.itwillbs.factron.controller.client;

import com.itwillbs.factron.dto.ResponseDTO;
import com.itwillbs.factron.dto.client.BusinessNumberDTO;
import com.itwillbs.factron.dto.client.RequestPostClientDTO;
import com.itwillbs.factron.dto.client.RequestPutClientDTO;
import com.itwillbs.factron.dto.client.ResponseClientDTO;
import com.itwillbs.factron.service.client.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/client")
@RequiredArgsConstructor
public class ClientRestController {

    private final ClientService clientService;

    @Value("${custom.api.secret}")
    private String API_SECRET_KEY;

    @GetMapping("")
    public ResponseDTO<List<ResponseClientDTO>> getClient(
            @RequestParam(required = false) String name
    ) {

        try {

            List<ResponseClientDTO> clientList = clientService.getClient(name);

            return ResponseDTO.success(clientList);
        } catch (Exception e) {
            return ResponseDTO.fail(
                    800,
                    "조회할 수 없습니다.",
                    null
            );
        }
    }

    @PostMapping("/openapi/businessnumber")
    public ResponseDTO<Boolean> validBusinessNumber(@Valid @RequestBody BusinessNumberDTO businessNumber) {

        try {
            return ResponseDTO.success(clientService.validBusinessNumber(businessNumber, API_SECRET_KEY));
        } catch (Exception e) {
            return ResponseDTO.fail(
                    800,
                    "검증에 실패했습니다.",
                    false
            );
        }
    }

    @PostMapping("")
    public ResponseDTO<Void> saveClient(@Valid @RequestBody List<RequestPostClientDTO> clientDTOList) {

        try {
            return ResponseDTO.success(clientService.saveClientList(clientDTOList));
        } catch (Exception e) {
            return ResponseDTO.fail(
                    800,
                    "저장에 실패했습니다.",
                    null
            );
        }
    }

    @PutMapping("")
    public ResponseDTO<Void> updateClient(@Valid @RequestBody List<RequestPutClientDTO> clientDTOList) {

        try {
            return ResponseDTO.success(clientService.updateClientList(clientDTOList));
        } catch (Exception e) {
            return ResponseDTO.fail(
                    800,
                    "수정에 실패했습니다.",
                    null
            );
        }
    }
}
