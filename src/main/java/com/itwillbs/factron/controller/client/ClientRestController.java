package com.itwillbs.factron.controller.client;

import com.itwillbs.factron.dto.ResponseDTO;
import com.itwillbs.factron.dto.client.ClientDTO;
import com.itwillbs.factron.dto.client.ResponseClientDTO;
import com.itwillbs.factron.service.client.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/sys/main")
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
}
