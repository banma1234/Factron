package com.itwillbs.factron.service.production;

import com.itwillbs.factron.dto.lot.RequestLotUpdateDTO;
import com.itwillbs.factron.dto.production.*;
import com.itwillbs.factron.entity.*;
import com.itwillbs.factron.entity.Process;
import com.itwillbs.factron.mapper.production.WorkOrderMapper;
import com.itwillbs.factron.mapper.production.WorkDetailMapper;
import com.itwillbs.factron.repository.employee.EmployeeRepository;
import com.itwillbs.factron.repository.process.LineRepository;
import com.itwillbs.factron.repository.process.ProcessHistoryRepository;
import com.itwillbs.factron.repository.process.ProcessRepository;
import com.itwillbs.factron.repository.product.ItemRepository;
import com.itwillbs.factron.repository.product.MaterialRepository;
import com.itwillbs.factron.repository.production.ProductionPlanningRepository;
import com.itwillbs.factron.repository.production.WorkOrderRepository;
import com.itwillbs.factron.repository.production.WorkerRepository;
import com.itwillbs.factron.repository.storage.OutboundRepository;
import com.itwillbs.factron.repository.storage.StockRepository;
import com.itwillbs.factron.repository.storage.StorageRepository;
import com.itwillbs.factron.service.lot.LotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Log4j2
public class WorkOrderServiceImpl implements WorkOrderService {

    private final WorkOrderMapper workOrderMapper;
    private final WorkOrderRepository workOrderRepository;
    private final ProductionPlanningRepository prdctPlanRepository;
    private final LineRepository lineRepository;
    private final ItemRepository itemRepository;
    private final MaterialRepository materialRepository;
    private final EmployeeRepository empRepository;
    private final StockRepository stockRepository;
    private final OutboundRepository outboundRepository;
    private final StorageRepository storageRepository;
    private final WorkerRepository workerRepository;
    private final LotService lotService;
    private final WorkDetailMapper workDetailMapper;
    private final ProcessRepository processRepository;
    private final ProcessHistoryRepository historyRepository;

    /*
    * 작업지시 목록 조회
    * */
    @Override
    public List<ResponseWorkOrderDTO> getWorkOrderList(RequestWorkOrderDTO requestWorkOrderDTO) {
        return workOrderMapper.getWorkOrderList(requestWorkOrderDTO);
    }

    /*
     * 작업지시 내릴 수 있는 제품 목록 조회
     * */
    @Override
    public List<ResponseWorkProdDTO> getWorkItemList(RequestWorkProdDTO requestWorkProdDTO) {
        return workOrderMapper.getWorkItemList(requestWorkProdDTO);
    }

    /*
     * 투입할 품목 목록 조회
     * */
    @Override
    public List<ResponseWorkProdDTO> getPossibleInputList(RequestWorkProdDTO requestWorkProdDTO) {
        return workOrderMapper.getPossibleInputList(requestWorkProdDTO);
    }

    /*
     * 작업 가능한 사원 목록 조회
     * */
    @Override
    public List<ResponseWorkerDTO> getPossibleWorkerList() {
        return workOrderMapper.getPossibleWorkerList();
    }

    /*
     * 작업지시 등록
     * */
    @Transactional
    @Override
    public Void registWorkOrder(RequestWorkOrderDTO requestWorkOrderDTO) {
        ProductionPlanning prdctPlan = prdctPlanRepository.findById(requestWorkOrderDTO.getPlanId())
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 생산계획입니다."));
        Line line = lineRepository.findById(requestWorkOrderDTO.getLineId())
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 라인입니다."));
        Item item = itemRepository.findById(requestWorkOrderDTO.getItemId())
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 제품입니다."));
        Employee employee = empRepository.findById(requestWorkOrderDTO.getEmpId())
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 사원입니다."));

        // 작업지시 등록
        WorkOrder workOrder = workOrderRepository.save(WorkOrder.builder()
                .id(generateWorkOrderId())
                .productionPlanning(prdctPlan)
                .item(item)
                .quantity(requestWorkOrderDTO.getQuantity())
                .statusCode("WKS001")
                .line(line)
                .employee(employee)
                .startDate(requestWorkOrderDTO.getStartDate())
                .build());

        // 작업자 등록
        List<Employee> empList = empRepository.findAllById(requestWorkOrderDTO.getWorkers());
        if(empList.size() != requestWorkOrderDTO.getWorkers().size()) {
            throw new NoSuchElementException("존재하지 않는 사원입니다.");
        }

        List<Worker> workers = empList.stream()
                .map(emp -> Worker.builder()
                        .workOrder(workOrder)
                        .employee(emp)
                        .build())
                .collect(Collectors.toList());

        workerRepository.saveAll(workers);

        // LOT 관련 전송 데이터
        List<RequestLotUpdateDTO> updateLotList = new ArrayList<>();

        for(RequestWorkProdDTO product : requestWorkOrderDTO.getInputProds()) {
            Stock stock;

            if(product.getProdId().startsWith("M")) {
                // 원자재
                Material material = materialRepository.findById(product.getProdId())
                        .orElseThrow(() -> new NoSuchElementException("존재하지 않는 품목입니다."));
                Storage storage = storageRepository.findByTypeCode("ITP001")
                        .orElseThrow(() -> new NoSuchElementException("존재하지 않는 창고입니다."));
                stock = stockRepository.findByMaterialAndStorage(material, storage)
                        .orElseThrow(() -> new NoSuchElementException("투입 품목의 재고가 없습니다."));

                // 원자재 출고
                outboundRepository.save(Outbound.builder()
                        .material(material)
                        .storage(storage)
                        .quantity(product.getQuantity())
                        .outDate(LocalDate.now())
                        .categoryCode("ITP001")
                        .statusCode("STS003") // 완료
                        .build());

                updateLotList.add(new RequestLotUpdateDTO(material.getId(), null, product.getQuantity(), workOrder));

            } else {
                // 반제품
                Item semiItem = itemRepository.findById(product.getProdId())
                        .orElseThrow(() -> new NoSuchElementException("존재하지 않는 품목입니다."));
                Storage storage = storageRepository.findByTypeCode("ITP002")
                        .orElseThrow(() -> new NoSuchElementException("존재하지 않는 창고입니다."));
                stock = stockRepository.findByItemAndStorage(semiItem, storage)
                        .orElseThrow(() -> new NoSuchElementException("투입 품목의 재고가 없습니다."));

                // 반제품 출고
                outboundRepository.save(Outbound.builder()
                        .item(semiItem)
                        .storage(storage)
                        .quantity(product.getQuantity())
                        .outDate(LocalDate.now())
                        .categoryCode("ITP002")
                        .statusCode("STS003") // 완료
                        .build());

                updateLotList.add(new RequestLotUpdateDTO(null, semiItem.getId(), product.getQuantity(), workOrder));
            }

            if(stock.getQuantity() < product.getQuantity()) {
                throw new IllegalArgumentException("투입 품목의 재고가 부족합니다.");
            }
            // 재고 감소
            stock.subtractQuantity(product.getQuantity());
        }

        // LOT 수량 감소 및 LOT_HISTORY 추가
        lotService.updateLotQuantity(updateLotList);

        return null;
    }

    /*
     * 투입된 품목 및 작업자 목록 조회
     * */
    @Override
    public ResponseWorkDetailDTO getWorkOrderDetail(String orderId) {
        return ResponseWorkDetailDTO.builder()
                .inputProds(workDetailMapper.getWorkProdList(orderId))
                .workers(workDetailMapper.getWorkerList(orderId))
                .build();
    }

    /*
     * 작업지시 시작
     * */
    @Transactional
    @Override
    public Void startWorkOrder(RequestWorkOrderDTO requestWorkOrderDTO) {
        // 라인 가동으로 변경
        Line line = lineRepository.findById(requestWorkOrderDTO.getLineId())
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 라인입니다."));
        if(line.getStatusCode().equals("LIS002")) {
            throw new IllegalArgumentException("가동중인 라인은 사용할 수 없습니다.");
        }
        line.updateStatusCode("LIS002"); // 가동

        // 작지 상태 변경
        WorkOrder workOrder = workOrderRepository.findById(requestWorkOrderDTO.getWorkOrderId())
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 작업지시입니다."));
        workOrder.updateStatus("WKS002"); // 생산중

        // 공정이력 대기 상태로 등록
        List<Process> processList = processRepository.findByLineId(requestWorkOrderDTO.getLineId());
        if(processList.isEmpty()) {
            throw new IllegalArgumentException("해당 라인에 등록된 공정이 없습니다.");
        }

        List<ProcessHistory> historyList = processList.stream()
                .map(process -> ProcessHistory.builder()
                        .process(process)
                        .workOrder(workOrder)
                        .statusCode("STS001") // 대기
                        .build())
                .collect(Collectors.toList());

        historyRepository.saveAll(historyList);
        return null;
    }



    // 작업지시 번호 생성
    private String generateWorkOrderId() {
        // 번호 형식 : WOyyyyMMdd-NNN
        String prefix = "WO" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        // 오늘 날짜 작업지시 수 조회
        long count = workOrderRepository.countByIdStartingWith(prefix);
        // count+1로 작업지시 번호 새로 생성
        String sequence = String.format("-%03d", count + 1);
        return prefix + sequence;
    }
}
