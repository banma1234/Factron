package com.itwillbs.factron.service.quality;

import com.itwillbs.factron.common.component.AuthorizationChecker;
import com.itwillbs.factron.dto.quality.*;
import com.itwillbs.factron.entity.Item;
import com.itwillbs.factron.entity.QualityInspection;
import com.itwillbs.factron.entity.QualityInspectionStandard;
import com.itwillbs.factron.mapper.quality.QualityInspectionStandardMapper;
import com.itwillbs.factron.repository.product.ItemRepository;
import com.itwillbs.factron.repository.quality.QualityInspectionRepository;
import com.itwillbs.factron.repository.quality.QualityInspectionStandardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class QualityInspectionServiceImpl implements QualityInspectionService {
    private final QualityInspectionRepository qualityInspectionRepository;
    private final QualityInspectionStandardRepository qualityInspectionStandardRepository;
    private final ItemRepository itemRepository;
    private final QualityInspectionStandardMapper qualityInspectionStandardMapper;
    private final AuthorizationChecker authorizationChecker;

    // 품질 검사 조회
    @Override
    @Transactional(readOnly = true)
    public List<ResponseInspSrhDTO> getQualityInspections(RequestInspSrhDTO requestDTO) {
        List<QualityInspection> qualityInspections;
        if (requestDTO.getQualityInspectionNameOrId() != null && !requestDTO.getQualityInspectionNameOrId().trim().isEmpty()) {
            qualityInspections = qualityInspectionRepository.findByNameOrIdContaining(requestDTO.getQualityInspectionNameOrId());
        } else {
            qualityInspections = qualityInspectionRepository.findAll();
        }
        
        return qualityInspections.stream()
                .map(ResponseInspSrhDTO::new)
                .collect(Collectors.toList());
    }

    // 품질 검사 추가 (여러 개)
    @Override
    public void registQualityInspection(List<RequestQualityInspectionDTO> inspections) {
        authorizationChecker.checkAnyAuthority("ATH003", "ATH006", "ATH007");
        for (RequestQualityInspectionDTO item : inspections) {
            QualityInspection qualityInspection = QualityInspection.builder()
                    .name(item.getInspectionName())
                    .inspectionType(item.getInspectionType())
                    .inspectionMethod(item.getInspectionMethod())
                    .build();
            
            qualityInspectionRepository.save(qualityInspection);
        }
    }

    // 품질 검사 수정 (여러 개)
    @Override
    public void updateQualityInspection(List<RequestQualityInspectionDTO> inspections) {
        authorizationChecker.checkAnyAuthority("ATH003", "ATH006", "ATH007");
        for (RequestQualityInspectionDTO item : inspections) {
            QualityInspection qualityInspection = qualityInspectionRepository.findById(item.getInspectionId())
                    .orElseThrow(() -> new RuntimeException("품질 검사를 찾을 수 없습니다. ID: " + item.getInspectionId()));
            
            qualityInspection = QualityInspection.builder()
                    .id(item.getInspectionId())
                    .name(item.getInspectionName())
                    .inspectionType(item.getInspectionType())
                    .inspectionMethod(item.getInspectionMethod())
                    .build();
            
            qualityInspectionRepository.save(qualityInspection);
        }
    }

    // 제품별 품질 검사 기준 조회
    @Override
    @Transactional(readOnly = true)
    public List<ResponseQualityStandardDTO> getQualityInspectionStandards(RequestInspStdSrhDTO requestDTO) {
        List<ResponseQualityStandardDTO> standards = qualityInspectionStandardMapper.getQualityInspectionByIdOrName(requestDTO);
        return standards;
    }

    @Override
    public List<ResponseQualityStandardDTO> getQualityInspectionStandardsByInspectionId(String inspectionId) {
        List<ResponseQualityStandardDTO> standards =qualityInspectionStandardRepository
                .findByQualityInspectionId(Long.parseLong(inspectionId))
                .stream()
                .map(ResponseQualityStandardDTO::new).collect(Collectors.toList());
        return standards;
    }

    // 제품별 품질 검사 기준 추가 (여러 개)
    @Override
    public void registQualityInspectionStandard(List<RequestQualityStandardDTO> standards) {
        authorizationChecker.checkAnyAuthority("ATH003", "ATH006", "ATH007");
        for (RequestQualityStandardDTO item : standards) {
            QualityInspection qualityInspection = qualityInspectionRepository.findById(item.getQualityInspectionId())
                    .orElseThrow(() -> new RuntimeException("품질 검사를 찾을 수 없습니다. ID: " + item.getQualityInspectionId()));
            
            Item product = itemRepository.findById(item.getItemId())
                    .orElseThrow(() -> new RuntimeException("제품을 찾을 수 없습니다. ID: " + item.getItemId()));

            if(qualityInspectionStandardRepository.existsByItemIdAndQualityInspectionId(product.getId(),qualityInspection.getId() )){
                throw new IllegalArgumentException("해당 제품에는 같은 품질검사가 등록되어있습니다.");
            }

            QualityInspectionStandard standard = QualityInspectionStandard.builder()
                    .qualityInspection(qualityInspection)
                    .item(product)
                    .targetValue(Double.parseDouble(item.getTargetValue()))
                    .upperLimit(item.getUpperLimit())
                    .lowerLimit(item.getLowerLimit())
                    .unit(item.getUnit())
                    .build();
            
            qualityInspectionStandardRepository.save(standard);
        }
    }

    // 제품별 품질 검사 기준 수정 (여러 개)
    @Override
    public void updateQualityInspectionStandard(List<RequestQualityStandardDTO> standards) {
        authorizationChecker.checkAnyAuthority("ATH003", "ATH006", "ATH007");
        for (RequestQualityStandardDTO item : standards) {
            QualityInspection qualityInspection = qualityInspectionRepository.findById(item.getQualityInspectionId())
                    .orElseThrow(() -> new RuntimeException("품질 검사를 찾을 수 없습니다. ID: " + item.getQualityInspectionId()));
            
            Item product = itemRepository.findById(item.getItemId())
                    .orElseThrow(() -> new RuntimeException("제품을 찾을 수 없습니다. ID: " + item.getItemId()));
            
            QualityInspectionStandard standard = QualityInspectionStandard.builder()
                    .id(item.getQualityInspectionStandardId())
                    .qualityInspection(qualityInspection)
                    .item(product)
                    .targetValue(Double.parseDouble(item.getTargetValue()))
                    .upperLimit(item.getUpperLimit())
                    .lowerLimit(item.getLowerLimit())
                    .unit(item.getUnit())
                    .build();
            
            qualityInspectionStandardRepository.save(standard);
        }
    }

    // 제품별 품질 검사 기준 삭제 (여러 개)
    @Override
    public void deleteQualityInspectionStandard(List<RequestQualityStandardDeleteDTO.DeleteItem> deleteList) {
        authorizationChecker.checkAnyAuthority("ATH003", "ATH006", "ATH007");
        for (RequestQualityStandardDeleteDTO.DeleteItem item : deleteList) {
            qualityInspectionStandardRepository.deleteById(item.getInspectionId());
        }
    }
}
