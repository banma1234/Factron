package com.itwillbs.factron.service.lot;

import com.itwillbs.factron.mapper.lot.LotMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LotServiceImpl implements LotService {

    private final LotMapper lotMapper;

    @Override
    public Long getLotSequence (Map<String, String> map) {
        return lotMapper.getLotSequence(map);
    }

}
