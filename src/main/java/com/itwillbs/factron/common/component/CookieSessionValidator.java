package com.itwillbs.factron.common.component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itwillbs.factron.entity.Employee;
import com.itwillbs.factron.repository.employee.EmployeeRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Base64;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Log4j2
public class CookieSessionValidator {
    
    private final EmployeeRepository employeeRepository;
    private final ObjectMapper objectMapper;
    
    /**
     * 쿠키의 세션 정보와 실제 인증 정보를 비교하여 검증
     */
    public void validateCookieSession() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        
        // 쿠키에서 사용자 정보 읽기
        Map<String, String> cookieUserInfo = getUserInfoFromCookie(request);
        if (cookieUserInfo == null) {
            log.warn("쿠키에서 사용자 정보를 찾을 수 없습니다.");
            throw new SecurityException("쿠키에서 사용자 정보를 찾을 수 없습니다.");
        }
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        // 1. 인증된 사용자 ID와 쿠키의 사용자 ID 비교
        if (!auth.getName().equals(cookieUserInfo.get("employeeId"))) {
            log.warn("세션 불일치 - 인증ID: {}, 쿠키ID: {}", auth.getName(), cookieUserInfo.get("employeeId"));
            throw new SecurityException("세션 정보가 일치하지 않습니다.");
        }
        
        // 2. 데이터베이스의 실제 사용자 정보와 쿠키 정보 비교
        Employee employee = employeeRepository.findById(Long.parseLong(cookieUserInfo.get("employeeId")))
                .orElseThrow(() -> new SecurityException("존재하지 않는 사용자입니다."));
        
        if (!employee.getName().equals(cookieUserInfo.get("employeeName")) ) {
            log.warn("사용자 정보 불일치 - DB: {}, 쿠키: {}", 
                employee.getName(), 
                cookieUserInfo.get("employeeName"));
            throw new SecurityException("사용자 정보가 일치하지 않습니다.");
        }
        
        log.debug("쿠키 세션 검증 성공 - 사용자: {}", cookieUserInfo.get("employeeId"));
    }
    
    /**
     * 쿠키에서 사용자 정보를 읽어옵니다.
     */
    public Map<String, String> getUserInfoFromCookie(HttpServletRequest request) {
        try {
            Cookie[] cookies = request.getCookies();
            if (cookies == null) {
                log.warn("쿠키가 없습니다.");
                return null;
            }

            for (Cookie cookie : cookies) {
                if ("EMPLOYEE_INFO".equals(cookie.getName())) {
                    String cookieValue = cookie.getValue();
                    if (cookieValue == null || cookieValue.isEmpty()) {
                        log.warn("EMPLOYEE_INFO 쿠키 값이 비어있습니다.");
                        return null;
                    }

                    // Base64 디코딩
                    String decodedValue = new String(Base64.getDecoder().decode(cookieValue));
                    log.debug("디코딩된 쿠키 값: {}", decodedValue);

                    // JSON을 Map으로 변환
                    Map<String, String> userInfo = objectMapper.readValue(decodedValue, new TypeReference<Map<String, String>>() {});
                    log.info("사용자 정보 로드 성공: {}", userInfo.get("employeeName"));
                    return userInfo;
                }
            }

            log.warn("EMPLOYEE_INFO 쿠키를 찾을 수 없습니다.");
            return null;

        } catch (Exception e) {
            log.error("쿠키에서 사용자 정보를 읽는 중 오류 발생: ", e);
            return null;
        }
    }
    
    /**
     * 사용자가 특정 권한을 가지고 있는지 확인합니다.
     */
    public boolean hasAuthority(HttpServletRequest request, String authority) {
        Map<String, String> userInfo = getUserInfoFromCookie(request);
        if (userInfo == null) {
            log.warn("사용자 정보를 가져올 수 없어 권한 확인을 건너뜁니다.");
            return false;
        }

        String authorities = userInfo.get("authorities");
        if (authorities == null) {
            log.warn("권한 정보가 없습니다.");
            return false;
        }

        // 권한 문자열을 쉼표로 분리하여 확인
        String[] authArray = authorities.split(",");
        for (String auth : authArray) {
            if (authority.equals(auth.trim())) {
                log.debug("권한 확인 성공: {} - {}", userInfo.get("employeeName"), authority);
                return true;
            }
        }

        log.warn("권한 확인 실패: {} - {}", userInfo.get("employeeName"), authority);
        return false;
    }
    
    /**
     * 사용자가 여러 권한 중 하나라도 가지고 있는지 확인합니다.
     */
    public boolean hasAnyAuthority(HttpServletRequest request, String... authorities) {
        for (String authority : authorities) {
            if (hasAuthority(request, authority)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 현재 로그인한 사용자의 사원번호를 가져옵니다.
     */
    public String getCurrentEmployeeId(HttpServletRequest request) {
        Map<String, String> userInfo = getUserInfoFromCookie(request);
        return userInfo != null ? userInfo.get("employeeId") : null;
    }
    
    /**
     * 현재 로그인한 사용자의 이름을 가져옵니다.
     */
    public String getCurrentEmployeeName(HttpServletRequest request) {
        Map<String, String> userInfo = getUserInfoFromCookie(request);
        return userInfo != null ? userInfo.get("employeeName") : null;
    }
} 