package com.itwillbs.factron.service.lot;

import com.itwillbs.factron.dto.lot.RequestInboundLotDTO;
import com.itwillbs.factron.entity.Lot;
import com.itwillbs.factron.repository.lot.LotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * LOT 번호를 생성하고 저장하는 서비스
 * */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LotCreateServiceImpl {

    private final LotRepository lotRepository;
    private LotService lotService;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.BASIC_ISO_DATE;

    /**
     * (입고) LOT생성
     * @param reqInbound (입고) LOT를 생성할 자재/물품 리스트
     * @return Void
     * */
    public Void CreateInboundLot(List<RequestInboundLotDTO> reqInbound) {

        String TODAY = LocalDateTime.now().format(DATE_FORMAT);
        Map<String, String> requiredElement = Map.of(
                "dateToday", TODAY,
                "eventType", reqInbound.getFirst().getEvent_type().getPrefix()
        );
        Long sequence = lotService.getLotSequence(requiredElement);

        // LOT번호 생성
        List<Lot> inboundLotList = createLots(reqInbound, TODAY, sequence);

        // LOT 테이블에 저장
        lotRepository.saveAll(inboundLotList);

        return null;
    }

    /**
     * 리스트를 순회하며 LOT 번호를 생성하고 부여하는 메소드
     * @param reqInbound LOT를 생성할 자재/물품 리스트
     * @param TODAY 오늘 날짜
     * @param sequence 초기 순서
     * @return inboundLotList (입고) LOT번호가 저장된 리스트
     * */
    private static List<Lot> createLots(List<RequestInboundLotDTO> reqInbound, String TODAY, Long sequence) {
        List<Lot> inboundLotList = new ArrayList<>();

        // 리스트 순환하며 각각의 물품, 자재에 LOT번호 부여
        for(int i = 0; i< reqInbound.size(); i++) {

            RequestInboundLotDTO DTO = reqInbound.get(i);

            // LOT번호 형식 : 20180516-INB-0001
            String LotId = String.format("%s-%s-%04d",
                    TODAY,
                    reqInbound.getFirst().getEvent_type(),
                    sequence + i
            );

            // LOT 생성
            Lot LOT = DTO.toEntity(LotId);

            inboundLotList.add(LOT);

        }
        return inboundLotList;
    }

}
