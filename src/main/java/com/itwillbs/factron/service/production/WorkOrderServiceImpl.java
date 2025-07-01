package com.itwillbs.factron.service.production;

import com.itwillbs.factron.dto.production.*;
import com.itwillbs.factron.entity.*;
import com.itwillbs.factron.mapper.production.WorkOrderMapper;
import com.itwillbs.factron.repository.employee.EmployeeRepository;
import com.itwillbs.factron.repository.process.LineRepository;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
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
     * 작업 제품 정보 및 투입 품목 목록 조회
     * */
    @Override
    public List<ResponseWorkProdDTO> getInputProdList(RequestWorkProdDTO requestWorkProdDTO) {
        return workOrderMapper.getInputProdList(requestWorkProdDTO);
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
                        .statusCode("STS001") // 대기
                        .build());

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
                        .statusCode("STS001") // 대기
                        .build());
            }

            if(stock.getQuantity() < product.getQuantity()) {
                throw new IllegalArgumentException("투입 품목의 재고가 부족합니다.");
            }
            // 재고 감소
            stock.subtractQuantity(product.getQuantity());
        }

        // LOT 수량 감소 및 LOT_HISTORY 추가
        // TODO: 미완성

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
        List<Worker> workers = new ArrayList<>();

        for(Long empId : requestWorkOrderDTO.getWorkers()) {
            Employee worker = empRepository.findById(empId).orElseThrow(() -> new NoSuchElementException("존재하지 않는 사원입니다."));
            workers.add(Worker.builder()
                    .workOrder(workOrder)
                    .employee(worker)
                    .build());
        }

        workerRepository.saveAll(workers);
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
