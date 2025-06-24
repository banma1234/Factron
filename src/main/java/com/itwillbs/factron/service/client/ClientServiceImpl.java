package com.itwillbs.factron.service.client;

import com.itwillbs.factron.dto.client.ResponseClientDTO;
import com.itwillbs.factron.entity.Client;
import com.itwillbs.factron.repository.client.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;

    @Override
    public List<ResponseClientDTO> getClient(String name) {

        List<Client> clientList;

        if(name == null || name.isEmpty()) {
            clientList = clientRepository.findAll();
        } else {
            clientList = clientRepository.findByNameContaining(name)
                    .orElseThrow(() -> new RuntimeException("조회할 수 없습니다."));
        }

        return toClientDTOList(clientList);
    }

    private List<ResponseClientDTO> toClientDTOList(List<Client> clientList) {

        return clientList.stream()
                .map(ResponseClientDTO :: fromEntity)
                .toList();
    }
}
