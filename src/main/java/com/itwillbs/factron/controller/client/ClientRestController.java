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

    /**
     * 공공 api 시크릿 키
     * */
    @Value("${custom.api.secret}")
    private String API_SECRET_KEY;

    /**
     * client 검색 및 조회
     * @param name 거래처명(필수X)
     * @return ResponseDTO client 반환 DTO
     * */
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
                    e.getMessage(),
                    null
            );
        }
    }

    /**
     * 사업자등록번호 검증
     * @param businessNumber 공공 api 요청 DTO
     * @return ResponseDTO
     * */
    @PostMapping("/openapi/businessnumber")
    public ResponseDTO<Boolean> validBusinessNumber(@Valid @RequestBody BusinessNumberDTO businessNumber) {

        try {
            return ResponseDTO.success(clientService.validBusinessNumber(businessNumber, API_SECRET_KEY));
        } catch (Exception e) {
            return ResponseDTO.fail(
                    800,
                    e.getMessage(),
                    false
            );
        }
    }

    /**
     * client 삽입
     * @param clientDTOList POST 요청 DTO
     * @return ResponseDTO
     * */
    @PostMapping("")
    public ResponseDTO<Void> saveClient(@Valid @RequestBody List<RequestPostClientDTO> clientDTOList) {

        try {
            return ResponseDTO.success(clientService.saveClientList(clientDTOList));
        } catch (Exception e) {
            return ResponseDTO.fail(
                    800,
                    e.getMessage(),
                    null
            );
        }
    }

    /**
     * client 수정
     * @param clientDTOList PUT 요청 DTO
     * @return ResponseDTO
     * */
    @PutMapping("")
    public ResponseDTO<Void> updateClient(@Valid @RequestBody List<RequestPutClientDTO> clientDTOList) {

        try {
            return ResponseDTO.success(clientService.updateClientList(clientDTOList));
        } catch (Exception e) {
            return ResponseDTO.fail(
                    800,
                    e.getMessage(),
                    null
            );
        }
    }
}
