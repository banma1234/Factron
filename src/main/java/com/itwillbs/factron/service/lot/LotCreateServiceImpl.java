package com.itwillbs.factron.service.lot;

import com.itwillbs.factron.common.component.AuthorizationChecker;
import com.itwillbs.factron.dto.lot.RequestInboundLotDTO;
import com.itwillbs.factron.dto.lot.RequestProcessLotDTO;
import com.itwillbs.factron.dto.lot.RequestQualityLotDTO;
import com.itwillbs.factron.entity.Lot;
import com.itwillbs.factron.repository.lot.LotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * LOT 번호를 생성하고 저장하는 서비스
 * */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Log4j2
public class LotCreateServiceImpl implements LotCreateService {

    private final LotRepository lotRepository;
    private final LotService lotService;
    private final AuthorizationChecker authorizationChecker;

    /**
     * (입고) LOT 생성
     * @param reqInbound (입고) LOT를 생성할 자재/물품 리스트
     * @return Void
     */
    @Override
    @Transactional
    public Void CreateInboundLot(List<RequestInboundLotDTO> reqInbound) {

        // 권한 검사 : 관리자, 재무팀, 생산팀, 작업반장
        if (!authorizationChecker.hasAnyAuthority("ATH003", "ATH005","ATH006", "ATH007")) {
            throw new SecurityException("권한이 없습니다.");
        }

        if (reqInbound.isEmpty()) {
            throw new NoSuchElementException("LOT 생성에 필요한 데이터가 없습니다.");
        }

        String EVENT_TYPE = reqInbound.getFirst().getEvent_type().getPrefix();
        // LOT_ID 생성에 필요한 데이터
        Map<String, Object> LotIdElement = getSequenceForToday(EVENT_TYPE);

        String TODAY = (String) LotIdElement.get("today");
        long count = (Long) LotIdElement.get("sequence");
        Long currentUser = authorizationChecker.getCurrentEmployeeId();

        List<Lot> lotList = new ArrayList<>();

        for (int i = 0; i < reqInbound.size(); i++) {

            // LOT_ID 생성
            String lotId = generateLotId(TODAY, EVENT_TYPE, count + i);
            // LOT 생성
            lotList.add(reqInbound.get(i).toEntity(lotId, currentUser));
        }

        lotRepository.saveAll(lotList);

        return null;
    }

    /**
     * (공정) LOT 생성
     * @param reqInbound (공정) LOT를 생성할 공정
     * @return Void
     * */
    @Override
    @Transactional
    public Lot CreateProcessLot(RequestProcessLotDTO reqInbound) {

        // 권한 검사 : 관리자, 생산팀, 작업반장
        if (!authorizationChecker.hasAnyAuthority("ATH003", "ATH006", "ATH007")) {
            throw new SecurityException("권한이 없습니다.");
        }

        String EVENT_TYPE = reqInbound.getEvent_type();
        // LOT_ID 생성에 필요한 데이터
        Map<String, Object> LotIdElement = getSequenceForToday(EVENT_TYPE);

        String TODAY = (String) LotIdElement.get("today");
        long count = (Long) LotIdElement.get("sequence");
        Long currentUser = authorizationChecker.getCurrentEmployeeId();

        // LOT_ID 생성
        String lotId = generateLotId(TODAY, EVENT_TYPE, count + 1);
        // LOT 생성
        Lot lot = reqInbound.toEntity(lotId, currentUser);

        return lotRepository.save(lot);
    }

    /**
     * (검사) LOT 생성
     * @param reqInbound (검사) LOT를 생성할 검사
     * @return Void
     * */
    @Override
    @Transactional
    public Lot CreateQualityLot(RequestQualityLotDTO reqInbound) {

        // 권한 검사 : 관리자, 생산팀, 작업반장
        if (!authorizationChecker.hasAnyAuthority("ATH003", "ATH006", "ATH007")) {
            throw new SecurityException("권한이 없습니다.");
        }

        String EVENT_TYPE = reqInbound.getEvent_type().getPrefix();
        // LOT_ID 생성에 필요한 데이터
        Map<String, Object> LotIdElement = getSequenceForToday(EVENT_TYPE);

        String TODAY = (String) LotIdElement.get("today");
        long count = (Long) LotIdElement.get("sequence");
        Long currentUser = authorizationChecker.getCurrentEmployeeId();

        // LOT_ID 생성
        String lotId = generateLotId(TODAY, EVENT_TYPE, count + 1);
        // LOT 생성
        Lot lot = reqInbound.toEntity(lotId, currentUser);

        return lotRepository.save(lot);
    }

    /**
     * LOT_ID 생성 메서드
     * @param date 오늘 날짜
     * @param eventType LOT 유형
     * @param sequence 순서
     * @return String
     */
    private String generateLotId(String date, String eventType, long sequence) {
        return String.format("%s-%s-%04d", date, eventType, sequence);
    }

    /**
     * 날짜 및 시퀀스 조회
     * @param eventTypePrefix LOT 유형
     * return Map
     */
    private Map<String, Object> getSequenceForToday(String eventTypePrefix) {
        String TODAY = LocalDateTime.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        
        // JPA Repository 사용 - 같은 트랜잭션 내에서 조회
        Long sequence = lotRepository.countByDateAndEventType(TODAY, eventTypePrefix);
        
        return Map.of("today", TODAY, "sequence", sequence);
    }

}