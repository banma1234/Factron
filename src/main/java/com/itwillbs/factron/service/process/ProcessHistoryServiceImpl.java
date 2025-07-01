package com.itwillbs.factron.service.process;

import com.itwillbs.factron.dto.lot.RequestProcessLotDTO;
import com.itwillbs.factron.dto.process.RequestProcessHistDTO;
import com.itwillbs.factron.dto.process.ResponseProcessHistoryInfoDTO;
import com.itwillbs.factron.entity.*;
import com.itwillbs.factron.repository.lot.LotHistoryRepository;
import com.itwillbs.factron.repository.lot.LotRepository;
import com.itwillbs.factron.repository.process.ProcessHistoryRepository;
import com.itwillbs.factron.repository.production.WorkOrderRepository;
import com.itwillbs.factron.service.lot.LotCreateService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional(readOnly = true)
public class ProcessHistoryServiceImpl implements ProcessHistoryService {
    private final ProcessHistoryRepository processHistoryRepository;
    private final LotHistoryRepository lotHistoryRepository;
    private final WorkOrderRepository workOrderRepository;
    private final LotRepository lotRepository;
    private final LotCreateService lotCreateService;

    @Override
    public List<ResponseProcessHistoryInfoDTO> getProcessHistoryList(String workOrderId) {
        return processHistoryRepository.findProcessHistoriesByWorkOrderId(workOrderId);
    }

    @Override
    @Transactional
    public void updateProcessHistory(RequestProcessHistDTO requestDTO) {
        String workOrderId = requestDTO.getWorkOrderId();

        WorkOrder workOrder = workOrderRepository.findById(workOrderId).orElseThrow(
                () -> new EntityNotFoundException("해당 작업지시가 없습니다.")
        );

        Item item = workOrder.getItem();

        for(RequestProcessHistDTO.ProcessDTO process : requestDTO.getProcessList()){


            // 흐음... 갯수 적어야 할려나?
            // CreateProcessLot 했을때 로트 리턴 받아야함
//            RequestProcessLotDTO newProc = new RequestProcessLotDTO(null, item, process.getProcessTypeCode, workOrderId);
//            Lot newLot = lotCreateService.CreateProcessLot(newProc);

            //TODO: get new Lot
            String newLotId = "TEST1";

            Integer currProcess = processHistoryRepository.countCompletedProcessHistoriesByWorkOrderId(workOrderId);
            log.info("currProcess : {}", currProcess);

            // 첫번째 공정일때 LOT_HISTORY load 전체 LOT_ID
            if(currProcess > 0){
                LotHistory prevLot = lotHistoryRepository.findFirstByWorkOrderIdOrderByCreatedAtDesc(workOrderId).orElseThrow(
                        () -> new EntityNotFoundException("해당 작업지시 로트번호가 존재하지 않습니다."));

                log.info("prevLot : {}", prevLot);

                //TODO: add new Lot Structure
            }else{
                List<LotHistory> prevLots = lotHistoryRepository.findByWorkOrderId(workOrderId);
                if(prevLots.size() <= 0){
                    throw new EntityNotFoundException("해당 작업지시 로트번호가 존재하지 않습니다.");
                }

                log.info("prevLots : {}", prevLots);

                //TODO: add new Lot Structure
            }



            Lot newLot = lotRepository.findById(newLotId).orElseThrow(
                    () -> new EntityNotFoundException("해당 로트번호가 없습니다.")
            );

            LotHistory newLotHist = LotHistory.builder()
                    .quantity(process.getOutputQty())
                    .createdAt(LocalDateTime.now())
                    .workOrder(workOrder)
                    .lot(newLot)
                    .build();

            lotHistoryRepository.save(newLotHist);

            // update process history
            ProcessHistory currProcHist = processHistoryRepository.findById(process.getProcessHistId()).orElseThrow(
                    () -> new EntityNotFoundException("해당 공정 이력이 없습니다.")
            );

            currProcHist.updateCoastTime(process.getCostTime());
            currProcHist.updateStartTime(process.getStartTime());
            currProcHist.updateLot(newLot);
            currProcHist.updateOutputQuantity(process.getOutputQty());
            currProcHist.updateStatus("STS003");
        }

    }


}
