package com.itwillbs.factron.service.client;

import com.itwillbs.factron.common.component.AuthorizationChecker;
import com.itwillbs.factron.dto.client.RequestClientDTO;
import com.itwillbs.factron.dto.client.ResponseClientDTO;
import com.itwillbs.factron.entity.Client;
import com.itwillbs.factron.repository.client.ClientRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final AuthorizationChecker authorizationChecker;

    /**
     * 거래처 정보 조회 및 검색
     * @param name 거래처명
     * @return responseClientDTO 반환 DTO
     * */
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

    /**
     * 거래처 정보 다중 저장
     * @param clientDTOList 거래처 정보 List
     * @return Void
     * */
    @Transactional
    @Override
    public Void saveClientList(@Valid List<RequestClientDTO> clientDTOList) {

        if(!authorizationChecker.hasAnyAuthority("ATH003", "ATH004")) {
            throw new SecurityException("권한이 없습니다.");
        }

        List<Client> clientList = toClientEntity(clientDTOList);
        clientRepository.saveAll(clientList);

        return null;
    }

    private List<ResponseClientDTO> toClientDTOList(List<Client> clientList) {

        return clientList.stream()
                .map(ResponseClientDTO :: fromEntity)
                .toList();
    }

    private List<Client> toClientEntity(List<RequestClientDTO> DTOList) {

        return DTOList.stream()
                .map(RequestClientDTO :: toEntity)
                .toList();
    }
}
