package com.itwillbs.factron.service.client;

import com.itwillbs.factron.dto.client.*;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ClientService {
    List<ResponseClientDTO> getClient(String name);

    Void saveClientList(@Valid List<RequestPostClientDTO> clientDTOList);

    Void updateClientList(@Valid List<RequestPutClientDTO> clientDTOList);

    Boolean validBusinessNumber(String businessNumber, String API_SECRET_KEY);
}
