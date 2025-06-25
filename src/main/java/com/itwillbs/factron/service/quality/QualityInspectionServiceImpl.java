package com.itwillbs.factron.service.quality;

import com.itwillbs.factron.dto.quality.ResponseInspSrhDTO;
import com.itwillbs.factron.entity.QualityInspection;
import com.itwillbs.factron.entity.QualityInspectionStandard;
import com.itwillbs.factron.repository.quality.QualityInspectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class QualityInspectionServiceImpl implements QualityInspectionService{
    private final QualityInspectionRepository qualityInspectionRepository;

    @Override
    public List<ResponseInspSrhDTO> getQualityInspections() {
        List<QualityInspection> qualityInspections = qualityInspectionRepository.findAll();
        return List.of();
    }

    @Override
    public void registQualityInspection(QualityInspection qualityInspection) {

    }

    @Override
    public void updateQualityInspection(QualityInspection qualityInspection) {

    }

    @Override
    public List<QualityInspectionStandard> getQualityInspectionStandards() {
        return List.of();
    }

    @Override
    public void registQualityInspectionStandard(QualityInspectionStandard qualityInspectionStandard) {

    }

    @Override
    public void updateQualityInspectionStandard(QualityInspectionStandard qualityInspectionStandard) {

    }

    @Override
    public void deleteQualityInspectionStandard(Long id) {

    }
}
