package com.itwillbs.factron.service.contract;

import com.itwillbs.factron.dto.contract.RequestRegisterContractDTO;
import com.itwillbs.factron.dto.contract.RequestSearchContractDTO;
import com.itwillbs.factron.dto.contract.ResponseContractItemDTO;
import com.itwillbs.factron.dto.contract.ResponseSearchContractDTO;
import com.itwillbs.factron.entity.*;
import com.itwillbs.factron.mapper.contract.ContractMapper;
import com.itwillbs.factron.repository.approval.ApprovalRepository;
import com.itwillbs.factron.repository.client.ClientRepository;
import com.itwillbs.factron.repository.contract.ContractListRepository;
import com.itwillbs.factron.repository.contract.ContractRepository;
import com.itwillbs.factron.repository.employee.EmployeeRepository;
import com.itwillbs.factron.repository.product.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ContractServiceImpl implements ContractService {

    private final ContractRepository contractRepository;
    private final ContractMapper contractMapper;
    private final ApprovalRepository approvalRepository;
    private final ContractListRepository contractListRepository;
    private final EmployeeRepository employeeRepository;
    private final ClientRepository clientRepository;
    private final ItemRepository itemRepository;

    // 수주 목록 조회
    @Override
    public List<ResponseSearchContractDTO> getContractsList(RequestSearchContractDTO requestSearchContractDTO) {
        return contractMapper.getContractsList(requestSearchContractDTO);
    }

    // 수주 품목 조회
    @Override
    public List<ResponseContractItemDTO> getContractItemsByContractId(Long contractId) {
        return contractMapper.getContractItemsByContractId(contractId);
    }

    @Override
    public ResponseSearchContractDTO getContractDetailByContractId(Long contractId){
        return contractMapper.getContractDetailByContractId(contractId);
    }

    @Transactional
    @Override
    public void registerContract(RequestRegisterContractDTO dto) {
        // 1. 결재 데이터 저장
        Approval approval = Approval.builder()
                .requester(employeeRepository.findById(dto.getEmployeeId())
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사원 ID")))
                .requestedAt(LocalDate.now())
                .approvalTypeCode("SLS001") // 예시: 수주 결재 코드
                .approvalStatusCode("APV001") // 결재대기
                .build();
        approvalRepository.save(approval);

        // 2. 수주 데이터 저장
        Contract contract = Contract.builder()
                .employee(approval.getRequester())
                .client(clientRepository.findById(dto.getClientId())
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 거래처 ID")))
                .deadline(dto.getDeadline())
                .statusCode("STP001") // 예시: 수주대기
                .createdAt(LocalDate.now())
                .approval(approval)
                .build();
        contractRepository.save(contract);

        // 3. 수주 품목 저장
        for (RequestRegisterContractDTO.ContractItemDTO itemDto : dto.getItems()) {
            Item item = itemRepository.findById(itemDto.getItemId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 품목 ID"));
            ContractList contractItem = ContractList.builder()
                    .contract(contract)
                    .item(item)
                    .unit(item.getUnit()) // Item 엔티티의 unit 사용
                    .quantity(itemDto.getQuantity())
                    .price(itemDto.getPrice())
                    .build();
            contractListRepository.save(contractItem);
        }
    }

    @Transactional
    @Override
    public void cancelContract(Long approvalId) {
        // 1. 결재 데이터 조회
        Approval approval = approvalRepository.findById(approvalId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 결재 ID"));

        // 2. 연관 수주 데이터 조회
        Contract contract = contractRepository.findByApproval(approval)
                .orElseThrow(() -> new IllegalArgumentException("결재에 연관된 수주 데이터가 없습니다."));

        // 3. 결재 삭제 (FK 해제)
        contract.setApproval(null);
        approvalRepository.delete(approval);

        // 4. 수주 상태를 취소로 변경
        contract.updateStatus("STP005");

        // 5. 변경 사항 저장
        contractRepository.save(contract);
    }
}
