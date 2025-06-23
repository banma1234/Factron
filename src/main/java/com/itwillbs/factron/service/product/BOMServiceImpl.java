package com.itwillbs.factron.service.product;

import com.itwillbs.factron.dto.product.RequestBOMDTO;
import com.itwillbs.factron.dto.product.ResponseBOMDTO;
import com.itwillbs.factron.mapper.product.BOMMapper;
import com.itwillbs.factron.repository.product.BomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BOMServiceImpl implements BOMService {

    private final BOMMapper bomMapper;
    private final BomRepository bomRepository;

    /*
     * BOM 목록 조회
     * */
    @Override
    public List<ResponseBOMDTO> getBOMList(RequestBOMDTO requestBOMDTO) {
        return bomMapper.getBOMList(requestBOMDTO);
    }
}
