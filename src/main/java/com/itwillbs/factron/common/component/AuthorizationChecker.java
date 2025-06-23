package com.itwillbs.factron.common.component;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 서비스 레벨에서 권한을 체크하는 공통 컴포넌트
 * CookieSessionValidator를 래핑하여 서비스에서 사용하기 편리하게 만든 클래스
 */
@Component
@RequiredArgsConstructor
@Log4j2
public class AuthorizationChecker {

    private final CookieSessionValidator cookieSessionValidator;

    /**
     * 현재 HTTP 요청을 가져옵니다.
     */
    private HttpServletRequest getCurrentRequest() {
        try {
            return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        } catch (Exception e) {
            log.warn("현재 요청을 가져올 수 없습니다: {}", e.getMessage());
            throw new SecurityException("요청 정보를 가져올 수 없습니다.");
        }
    }

    // ========== 권한 체크 메서드들 (예외 발생) ==========

    /**
     * 사원 관리 권한을 체크합니다 (ATH002, ATH003)
     */
    public void checkEmployeeManagementAuthority() {
        checkAnyAuthority("ATH002", "ATH003");
    }

    /**
     * 관리자 권한을 체크합니다 (ATH003)
     */
    public void checkAdminAuthority() {
        checkAuthority("ATH003");
    }

    /**
     * 인사 직원 권한을 체크합니다 (ATH002)
     */
    public void checkHRAuthority() {
        checkAuthority("ATH002");
    }

    /**
     * 특정 권한을 체크합니다
     */
    public void checkAuthority(String authority) {
        if (!hasAuthority(authority)) {
            log.warn("권한 없음 - 사용자: {}, 필요 권한: {}", 
                    getCurrentEmployeeName(), authority);
            throw new SecurityException("권한이 없습니다: " + authority);
        }
    }

    /**
     * 여러 권한 중 하나라도 있는지 체크합니다
     */
    public void checkAnyAuthority(String... authorities) {
        if (!hasAnyAuthority(authorities)) {
            log.warn("권한 없음 - 사용자: {}, 필요 권한: {}", 
                    getCurrentEmployeeName(), String.join(", ", authorities));
            throw new SecurityException("권한이 없습니다.");
        }
    }

    // ========== 권한 확인 메서드들 (boolean 반환) ==========

    /**
     * 특정 권한이 있는지 확인합니다 (예외 발생 없이 boolean 반환)
     */
    public boolean hasAuthority(String authority) {
        HttpServletRequest request = getCurrentRequest();
        return cookieSessionValidator.hasAuthority(request, authority);
    }

    /**
     * 여러 권한 중 하나라도 있는지 확인합니다 (예외 발생 없이 boolean 반환)
     */
    public boolean hasAnyAuthority(String... authorities) {
        HttpServletRequest request = getCurrentRequest();
        return cookieSessionValidator.hasAnyAuthority(request, authorities);
    }

    // ========== 사용자 정보 가져오기 ==========

    /**
     * 현재 로그인한 사용자의 사원번호를 가져옵니다.
     */
    public String getCurrentEmployeeId() {
        HttpServletRequest request = getCurrentRequest();
        return cookieSessionValidator.getCurrentEmployeeId(request);
    }

    /**
     * 현재 로그인한 사용자의 이름을 가져옵니다.
     */
    public String getCurrentEmployeeName() {
        HttpServletRequest request = getCurrentRequest();
        return cookieSessionValidator.getCurrentEmployeeName(request);
    }

    // ========== 로깅 메서드들 ==========

    /**
     * 현재 로그인한 사용자의 정보를 로그에 기록합니다.
     */
    public void logCurrentUser(String action, String target) {
        String currentUser = getCurrentEmployeeName();
        log.info("{} - 사용자: {}, 대상: {}", action, currentUser, target);
    }

    /**
     * 권한 체크와 함께 사용자 정보를 로그에 기록합니다.
     */
    public void checkAuthorityAndLog(String authority, String action, String target) {
        checkAuthority(authority);
        logCurrentUser(action, target);
    }

    /**
     * 사원 관리 권한 체크와 함께 사용자 정보를 로그에 기록합니다.
     */
    public void checkEmployeeManagementAndLog(String action, String target) {
        checkEmployeeManagementAuthority();
        logCurrentUser(action, target);
    }

    /**
     * 관리자 권한 체크와 함께 사용자 정보를 로그에 기록합니다.
     */
    public void checkAdminAndLog(String action, String target) {
        checkAdminAuthority();
        logCurrentUser(action, target);
    }

    // ========== 고급 기능들 ==========

    /**
     * 본인 정보인지 확인합니다.
     */
    public boolean isOwnInfo(String targetEmployeeId) {
        String currentEmployeeId = getCurrentEmployeeId();
        return targetEmployeeId.equals(currentEmployeeId);
    }

    /**
     * 본인 정보이거나 특정 권한이 있는지 확인합니다.
     */
    public boolean isOwnInfoOrHasAuthority(String targetEmployeeId, String authority) {
        return isOwnInfo(targetEmployeeId) || hasAuthority(authority);
    }

    /**
     * 본인 정보이거나 여러 권한 중 하나라도 있는지 확인합니다.
     */
    public boolean isOwnInfoOrHasAnyAuthority(String targetEmployeeId, String... authorities) {
        return isOwnInfo(targetEmployeeId) || hasAnyAuthority(authorities);
    }
} 