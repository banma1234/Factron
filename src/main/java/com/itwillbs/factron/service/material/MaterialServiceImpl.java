package com.itwillbs.factron.service.material;

import com.itwillbs.factron.dto.material.RequestMaterialDTO;
import com.itwillbs.factron.dto.material.ResponseMaterialDTO;
import com.itwillbs.factron.entity.Material;
import com.itwillbs.factron.mapper.material.MaterialMapper;
import com.itwillbs.factron.repository.product.MaterialRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Log4j2
@Service
@RequiredArgsConstructor
public class MaterialServiceImpl implements MaterialService {

    private final MaterialMapper materialMapper;
    private final MaterialRepository materialRepository;

    @Override
    public List<ResponseMaterialDTO> getMaterialList(RequestMaterialDTO dto) {
        return materialMapper.getMaterialList(dto);
    }


    @Override
    public Void addMaterial(RequestMaterialDTO dto) {
        if (materialRepository.findById(dto.getMaterialId()).isPresent()) {
            throw new IllegalArgumentException("이미 등록된 ID 입니다");
        }


        Material material = Material.builder()
                .id(dto.getMaterialId())
                .name(dto.getName())
                .unit(dto.getUnit())
                .info(dto.getInfo())
                .spec(dto.getSpec())
                .createdBy(dto.getCreatedBy())
                .build();

        materialRepository.save(material);
        return null;
    }


    @Transactional
    @Override
    public Void updateMaterial(RequestMaterialDTO dto) {

        Material material = materialRepository
                .findById(dto.getMaterialId())
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 자재 입니다."));

        material.updateMaterial(dto);

        return null;
    }

    @Override
    public String getMaterialByCode(String code) {

        Material material = materialRepository.findById(code)
                .orElseThrow(() -> new NoSuchElementException("해당하는 자재가 없습니다."));

        return material.getName();
    }
}
