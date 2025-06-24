package com.itwillbs.factron.controller.client;

import com.itwillbs.factron.dto.ResponseDTO;
import com.itwillbs.factron.dto.client.RequestPostClientDTO;
import com.itwillbs.factron.dto.client.RequestPutClientDTO;
import com.itwillbs.factron.dto.client.ResponseClientDTO;
import com.itwillbs.factron.service.client.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/client")
@RequiredArgsConstructor
public class ClientRestController {

    private final ClientService clientService;

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
