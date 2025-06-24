package com.itwillbs.factron.service.client;

import com.itwillbs.factron.dto.client.ClientDTO;
import com.itwillbs.factron.dto.client.RequestClientDTO;
import com.itwillbs.factron.dto.client.ResponseClientDTO;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ClientService {
    List<ResponseClientDTO> getClient(String name);

    Void saveClientList(@Valid List<RequestClientDTO> clientDTOList);

}
