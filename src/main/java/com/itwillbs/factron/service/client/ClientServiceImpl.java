package com.itwillbs.factron.service.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.itwillbs.factron.common.component.AuthorizationChecker;
import com.itwillbs.factron.dto.client.BusinessNumberDTO;
import com.itwillbs.factron.dto.client.RequestPostClientDTO;
import com.itwillbs.factron.dto.client.RequestPutClientDTO;
import com.itwillbs.factron.dto.client.ResponseClientDTO;
import com.itwillbs.factron.entity.Client;
import com.itwillbs.factron.repository.client.ClientRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

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

        // 파라미터 존재시 검색, 그렇지 않으면 전체조회
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
    public Void saveClientList(@Valid List<RequestPostClientDTO> clientDTOList) {

        // 재무, 관리자 권한 체크
        if(!authorizationChecker.hasAnyAuthority("ATH003", "ATH004")) {
            throw new SecurityException("권한이 없습니다.");
        }

        List<Client> clientList = toClientEntity(clientDTOList);
        clientRepository.saveAll(clientList);

        return null;
    }

    /**
     * 거래처 정보 다중 수정
     * @param clientDTOList PUT 요청 DTO List
     * @return Void
     * */
    @Transactional
    @Override
    public Void updateClientList(@Valid List<RequestPutClientDTO> clientDTOList) {

        // 재무, 관리자 권한 체크
        if(!authorizationChecker.hasAnyAuthority("ATH003", "ATH004")) {
            throw new SecurityException("권한이 없습니다.");
        }

        List<Long> targetIdList = clientDTOList.stream()
                .map(RequestPutClientDTO :: getId)
                .toList();

        try {
            Map<Long, Client> clientMap = clientRepository.findAllById(targetIdList)
                    .stream()
                    .collect(Collectors.toMap(Client::getId, Function.identity()));

            clientDTOList.forEach(target -> {

                Client client = clientMap.get(target.getId());
                client.updateClient(target);
            });
        } catch (Exception e) {
            throw new NoSuchElementException("수정 대상이 존재하지 않습니다.");
        }

        return null;
    }

    /**
     * 사업자등록번호 검증. 외부에 api 요청 보내고 그 결과값을 뷰로 전달
     * @param businessNumber 사업자등록번호 객체
     * @param API_SECRET_KEY 공공 api 시크릿 키
     * return Boolean
     * */
    @Override
    public Boolean validBusinessNumber(BusinessNumberDTO businessNumber, String API_SECRET_KEY) throws JsonProcessingException {

        WebClient webClient = WebClient.create();
        String OPENAPI_URL = "https://api.odcloud.kr/api/nts-businessman/v1/status?serviceKey=" + API_SECRET_KEY;

        Mono<JsonNode> result = webClient.post()
                .uri(OPENAPI_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(businessNumber)
                .retrieve()
                .bodyToMono(JsonNode.class);

        String statusCode = Objects.requireNonNull(result.block()).get("status_code").asText();

        return statusCode.equals("OK");
    }

    /**
     * Entity => DTO 변환
     * */
    private List<ResponseClientDTO> toClientDTOList(List<Client> clientList) {

        return clientList.stream()
                .map(ResponseClientDTO :: fromEntity)
                .toList();
    }

    /**
     * DTO => Entity 변환
     * */
    private List<Client> toClientEntity(List<RequestPostClientDTO> DTOList) {

        return DTOList.stream()
                .map(RequestPostClientDTO :: toEntity)
                .toList();
    }
}
