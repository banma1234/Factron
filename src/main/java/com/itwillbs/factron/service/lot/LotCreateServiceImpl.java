package com.itwillbs.factron.service.lot;

import com.itwillbs.factron.dto.lot.RequestInboundLotDTO;
import com.itwillbs.factron.entity.Lot;
import com.itwillbs.factron.entity.LotHistory;
import com.itwillbs.factron.repository.lot.LotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LotCreateServiceImpl {

    private final LotRepository lotRepository;
    private LotService lotService;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.BASIC_ISO_DATE;

    public Void CreateInboundLot(List<RequestInboundLotDTO> reqInbound) {

        String TODAY = LocalDateTime.now().format(DATE_FORMAT);
        Map<String, String> requiredElement = Map.of(
                "dateToday", TODAY,
                "eventType", reqInbound.getFirst().getEvent_type().getPrefix()
        );
        Long sequence = lotService.getLotSequence(requiredElement);

        List<Lot> inboundLotList = new ArrayList<>();

        for(int i=0; i<reqInbound.size(); i++) {

            RequestInboundLotDTO DTO = reqInbound.get(i);

            String LotId = String.format("%s-%s-%04d",
                    TODAY,
                    reqInbound.getFirst().getEvent_type(),
                    sequence + i
            );

            Lot LOT = DTO.toEntity(LotId);

            inboundLotList.add(LOT);

        }

        lotRepository.saveAll(inboundLotList);

        return null;
    }

}
