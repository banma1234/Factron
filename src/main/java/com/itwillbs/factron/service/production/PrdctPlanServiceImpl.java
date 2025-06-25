package com.itwillbs.factron.service.production;

import com.itwillbs.factron.dto.production.RequestPrdctPlanDTO;
import com.itwillbs.factron.dto.production.ResponsePrdctPlanDTO;
import com.itwillbs.factron.entity.*;
import com.itwillbs.factron.mapper.production.PrdctPlanMapper;
import com.itwillbs.factron.repository.employee.EmployeeRepository;
import com.itwillbs.factron.repository.product.ItemRepository;
import com.itwillbs.factron.repository.production.ProductionPlanningRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PrdctPlanServiceImpl implements PrdctPlanService {

    private final PrdctPlanMapper prdctPlanMapper;
    private final ProductionPlanningRepository prdctPlanRepository;
    private final ItemRepository itemRepository;
    private final EmployeeRepository empRepository;

    /*
    * 생산계획 목록 조회
    * */
    @Override
    public List<ResponsePrdctPlanDTO> getPrdctPlanList(RequestPrdctPlanDTO requestPrdctPlanDTO) {
        return prdctPlanMapper.getPrdctPlanList(requestPrdctPlanDTO);
    }

    /*
     * 생산계획 등록
     * */
    @Transactional
    @Override
    public Void registPrdctPlan(RequestPrdctPlanDTO requestPrdctPlanDTO) {
        Item item = itemRepository.findById(requestPrdctPlanDTO.getItemId())
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 제품입니다."));
        Employee employee = empRepository.findById(requestPrdctPlanDTO.getEmpId())
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 사원입니다."));

        // 생산계획 등록
        prdctPlanRepository.save(ProductionPlanning.builder()
                .id(generatePlanningId())
                .employee(employee)
                .item(item)
                .quantity(requestPrdctPlanDTO.getQuantity())
                .startDate(requestPrdctPlanDTO.getStartDate())
                .endDate(requestPrdctPlanDTO.getEndDate())
                .build());
        return null;
    }



    // 생산계획 번호 생성
    private String generatePlanningId() {
        // 번호 형식 : PPyyyyMMdd-NNN
        String prefix = "PP" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        // 오늘 날짜 생산계획 수 조회
        long count = prdctPlanRepository.countByIdStartingWith(prefix);
        // count+1로 생산계획 번호 새로 생성
        String sequence = String.format("-%03d", count + 1);
        return prefix + sequence;
    }
}
