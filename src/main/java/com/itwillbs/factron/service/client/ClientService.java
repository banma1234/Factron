package com.itwillbs.factron.service.client;

import com.itwillbs.factron.dto.client.ResponseClientDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ClientService {
    List<ResponseClientDTO> getClient(String name);
}
