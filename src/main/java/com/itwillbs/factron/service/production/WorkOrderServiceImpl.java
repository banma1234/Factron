package com.itwillbs.factron.service.production;

import com.itwillbs.factron.dto.production.RequestWorkOrderDTO;
import com.itwillbs.factron.dto.production.ResponseWorkOrderDTO;
import com.itwillbs.factron.entity.Employee;
import com.itwillbs.factron.entity.Item;
import com.itwillbs.factron.entity.Line;
import com.itwillbs.factron.entity.ProductionPlanning;
import com.itwillbs.factron.mapper.production.WorkOrderMapper;
import com.itwillbs.factron.repository.employee.EmployeeRepository;
import com.itwillbs.factron.repository.process.LineRepository;
import com.itwillbs.factron.repository.product.ItemRepository;
import com.itwillbs.factron.repository.production.ProductionPlanningRepository;
import com.itwillbs.factron.repository.production.WorkOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NoSuchElementException;

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
    private final EmployeeRepository empRepository;

    /*
    * 작업지시 목록 조회
    * */
    @Override
    public List<ResponseWorkOrderDTO> getWorkOrderList(RequestWorkOrderDTO requestWorkOrderDTO) {
        log.info(requestWorkOrderDTO);
        return workOrderMapper.getWorkOrderList(requestWorkOrderDTO);
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
        // 작업자 등록
        // 자재/반제품 출고
        // 재고 감소
        // LOT 수량 변경
        // LOT_HISTORY 추가
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
