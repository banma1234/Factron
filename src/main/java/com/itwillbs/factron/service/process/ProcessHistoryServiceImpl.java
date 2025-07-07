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
import com.itwillbs.factron.service.lotStructure.LotStructureService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private final LotStructureService lotStructureService;

    @Override
    public List<ResponseProcessHistoryInfoDTO> getProcessHistoryList(String workOrderId) {
        return processHistoryRepository.findProcessHistoriesByWorkOrderId(workOrderId);
    }

    @Override
    @Transactional
    public void updateProcessHistory(RequestProcessHistDTO requestDTO) {
        String workOrderId = requestDTO.getWorkOrderId();
        Long inputQty = null;
        WorkOrder workOrder = workOrderRepository.findById(workOrderId).orElseThrow(
                () -> new EntityNotFoundException("해당 작업지시가 없습니다.")
        );

        Item item = workOrder.getItem();
        // DTO에 LIST로 들어온 공정 이력 처리
        for(RequestProcessHistDTO.ProcessDTO process : requestDTO.getProcessList()){

            // 새로운 LOT 생성
            RequestProcessLotDTO newProc = new RequestProcessLotDTO(null, item, process.getProcessTypeCode(), workOrderId);
            Lot newLot = lotCreateService.CreateProcessLot(newProc);
            log.info("LOT 생성 완료: lotId={}", newLot.getId());

            // 현재 공정 중인 공정이력 찾기
            Integer currProcess = processHistoryRepository.countCompletedProcessHistoriesByWorkOrderId(workOrderId);
            log.info("currProcess : {}", currProcess);


            if(currProcess > 0){ // 첫번째 공정이 아닐경우 가장 최근 LOT_HISTORY 가져오기
                LotHistory prevLot = lotHistoryRepository.findFirstByWorkOrderIdOrderByCreatedAtDesc(workOrderId).orElseThrow(
                        () -> new EntityNotFoundException("해당 작업지시 로트번호가 존재하지 않습니다."));
                inputQty = prevLot.getQuantity();
                //Lot 가져오기
                List<Lot> prevLotList = new ArrayList<>();
                prevLotList.add(prevLot.getLot());

                log.info("Lot : {}", prevLotList);

                // LOT Structure 추가하기
                try {
                    lotStructureService.linkLotStructure(newLot, prevLotList);
                    log.info("LOT Structure 연결 성공: parent={}, children={}", newLot.getId(), prevLotList.stream().map(Lot::getId).toList());
                } catch (Exception e) {
                    log.error("LOT Structure 연결 실패: {}", e.getMessage(), e);
                }
            }
            else{ // 첫번째 공정일때 LOT_HISTORY 전체 목록 가져오기
                List<LotHistory> prevLots = lotHistoryRepository.findByWorkOrderId(workOrderId);
                if(prevLots.size() <= 0){
                    throw new EntityNotFoundException("해당 작업지시 로트번호가 존재하지 않습니다.");
                }
                inputQty = workOrder.getQuantity();
                //Lot 가져오기
                List<Lot> prevLotList = prevLots.stream().map(LotHistory::getLot).toList();
                log.info("prevLotList : {}", prevLotList);

                // LOT Structure 추가하기
                try {
                    lotStructureService.linkLotStructure(newLot, prevLotList);
                    log.info("LOT Structure 연결 성공: parent={}, children={}", newLot.getId(), prevLotList.stream().map(Lot::getId).toList());
                } catch (Exception e) {
                    log.error("LOT Structure 연결 실패: {}", e.getMessage(), e);
                }
            }

            // 새로운 lot history 만들기
            LotHistory newLotHist = LotHistory.builder()
                    .quantity(process.getOutputQty())
                    .createdAt(LocalDateTime.now())
                    .workOrder(workOrder)
                    .lot(newLot)
                    .build();
            try{
                lotHistoryRepository.save(newLotHist);
                log.info("Lot History 연결 성공 id = {}", newLotHist.getId());
            }catch (Exception e){
                log.error("Lot History 연결 실패: {}", e.getMessage());
            }


            // 공정 이력 업데이트
            ProcessHistory currProcHist = processHistoryRepository.findById(process.getProcessHistId()).orElseThrow(
                    () -> new EntityNotFoundException("해당 공정 이력이 없습니다.")
            );

            currProcHist.updateCoastTime(process.getCostTime());
            currProcHist.updateStartTime(process.getStartTime());
            currProcHist.updateLot(newLot);
            currProcHist.updateOutputQuantity(process.getOutputQty());
            currProcHist.updateStatus("STS003");
            if(inputQty != null) currProcHist.updateInputQuantity(inputQty);
            try{
                processHistoryRepository.save(currProcHist);
                log.info("Process History 연결 성공 id = {}", currProcHist.getId());
            }catch (Exception e){
                log.error("Process History 연결 실패: {}", e.getMessage());
            }

        }

    }


}
