package com.itwillbs.factron.controller.test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itwillbs.factron.common.component.CookieSessionValidator;
import com.itwillbs.factron.dto.test.RequestTest;
import com.itwillbs.factron.dto.ResponseDTO;
import com.itwillbs.factron.dto.test.Test;
import com.itwillbs.factron.service.test.TestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.http.HttpStatus;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Base64;


@Slf4j
@RestController
@RequestMapping("/testList")
@RequiredArgsConstructor
public class TestRestController {

    private final TestService testService;
    private final CookieSessionValidator cookieValidator;
    private final ObjectMapper objectMapper;

    /*
    * 테스트 목록 조회
    */
    @GetMapping()
    public ResponseDTO<List<Test>> getTestList(RequestTest srhTest) {
        try {
            return ResponseDTO.success(testService.getTestList(srhTest));
        } catch (Exception e) {
            return ResponseDTO.fail(800, "퇴사 처리된 사원입니다.", testService.getTestList(srhTest));
        }
    }

    /*
     * 테스트 저장
     */
    @PostMapping()
    public ResponseDTO<Void> registTest(@RequestBody Test test) {
        log.info("registTest: {}", test);
        try {
            testService.registTest(test);
            return ResponseDTO.success("저장이 완료되었습니다!", null);
        } catch (Exception e) {
            return ResponseDTO.fail(800, "저장에 실패했습니다. 다시 시도해주세요.", null);
        }
    }

    @GetMapping("/session")
    public ResponseEntity<Map<String, Object>> getSessionInfo() {
        log.debug("세션 정보 조회 요청");
        
        try {
            // 현재 요청에서 쿠키 정보 가져오기
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            
            // 1. 모든 쿠키 정보 로깅
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                log.debug("요청에 포함된 쿠키들:");
                for (Cookie cookie : cookies) {
                    log.debug("  {} = {}", cookie.getName(), cookie.getValue());
                }
            } else {
                log.warn("요청에 쿠키가 없습니다.");
            }
            
            // 2. EMPLOYEE_INFO 쿠키에서 사용자 정보 추출
            Map<String, String> userInfo = extractUserInfoFromCookie(request);
            
            if (userInfo == null) {
                Map<String, Object> errorInfo = new HashMap<>();
                errorInfo.put("error", "EMPLOYEE_INFO 쿠키를 찾을 수 없거나 파싱할 수 없습니다.");
                errorInfo.put("timestamp", LocalDateTime.now());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorInfo);
            }
            
            // 3. 응답 데이터 구성
            Map<String, Object> response = new HashMap<>();
            response.put("employeeId", userInfo.get("employeeId"));
            response.put("employeeName", userInfo.get("employeeName"));
            response.put("authorities", userInfo.get("authorities"));
            response.put("message", "쿠키에서 사용자 정보 추출 성공");
            response.put("timestamp", LocalDateTime.now());
            response.put("requestURI", request.getRequestURI());
            
            log.info("세션 정보 조회 성공: {}", response);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("세션 정보 조회 실패", e);
            
            Map<String, Object> errorInfo = new HashMap<>();
            errorInfo.put("error", e.getMessage());
            errorInfo.put("timestamp", LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorInfo);
        }
    }
    
    /**
     * 쿠키에서 사용자 정보 추출 - 상세 로직
     */
    private Map<String, String> extractUserInfoFromCookie(HttpServletRequest request) {
        log.debug("쿠키에서 사용자 정보 추출 시작");
        
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            log.warn("요청에 쿠키가 없습니다.");
            return null;
        }
        
        for (Cookie cookie : cookies) {
            if ("EMPLOYEE_INFO".equals(cookie.getName())) {
                log.debug("EMPLOYEE_INFO 쿠키 발견: {}", cookie.getValue());
                
                try {
                    // Base64 디코딩
                    String encodedValue = cookie.getValue();
                    byte[] decodedBytes = Base64.getDecoder().decode(encodedValue);
                    String jsonString = new String(decodedBytes, "UTF-8");
                    
                    log.debug("디코딩된 JSON: {}", jsonString);
                    
                    // JSON 파싱
                    Map<String, String> userInfo = objectMapper.readValue(
                        jsonString, 
                        new TypeReference<Map<String, String>>() {}
                    );
                    
                    log.info("사용자 정보 추출 성공: {}", userInfo);
                    return userInfo;
                    
                } catch (Exception e) {
                    log.error("쿠키 파싱 실패", e);
                    return null;
                }
            }
        }
        
        log.warn("EMPLOYEE_INFO 쿠키를 찾을 수 없습니다.");
        return null;
    }
    
    /**
     * 현재 사용자 정보를 이용한 간단한 작업 예시
     */
    @GetMapping("/user-action")
    public ResponseEntity<Map<String, Object>> performUserAction() {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            Map<String, String> userInfo = extractUserInfoFromCookie(request);
            
            if (userInfo == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "사용자 정보를 찾을 수 없습니다."));
            }
            
            // 사용자 정보를 이용한 작업 수행
            String employeeId = userInfo.get("employeeId");
            String employeeName = userInfo.get("employeeName");
            String authorities = userInfo.get("authorities");
            
            Map<String, Object> result = new HashMap<>();
            result.put("message", "사용자 작업 수행 완료");
            result.put("performedBy", employeeName + "(" + employeeId + ")");
            result.put("userAuthorities", authorities);
            result.put("timestamp", LocalDateTime.now());
            
            log.info("사용자 작업 수행: {} - {}", employeeName, employeeId);
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("사용자 작업 수행 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }
}
