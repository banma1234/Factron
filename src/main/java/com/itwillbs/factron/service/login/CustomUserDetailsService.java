package com.itwillbs.factron.service.login;

import com.itwillbs.factron.entity.Employee;
import com.itwillbs.factron.entity.IntergratAuth;
import com.itwillbs.factron.repository.employee.EmployeeRepository;
import com.itwillbs.factron.repository.employee.IntergratAuthRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
@RequiredArgsConstructor
@Log4j2
public class CustomUserDetailsService implements UserDetailsService {

    private final IntergratAuthRepository intergratAuthRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String employeeId) throws UsernameNotFoundException {
        // 로그인 시도 로깅
        log.info("로그인 시도 - 사원번호: {}", employeeId);
        Employee employee = employeeRepository.findById(Long.parseLong(employeeId))
                .orElseThrow(() -> {
                    log.warn("사용자를 찾을 수 없습니다: {}", employeeId);
                    return new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + employeeId);
                });

        // 재직 상태 확인
        IntergratAuth auth = intergratAuthRepository.findByEmployeeId(Long.parseLong(employeeId)).orElseThrow(
                () -> new UsernameNotFoundException("사용자의 로그인 정보를 찾을 수 없습니다: " + employeeId)
        );

        if (!"Y".equals(auth.getIsActive())) {
            log.warn("퇴직한 사용자 로그인 시도: {}", employeeId);
            throw new UsernameNotFoundException("퇴직한 사용자입니다: " + employeeId);
        }

        // 권한 코드에 따른 역할 부여
        String role = getRoleByAuthCode(auth.getAuthCode());
        log.info("로그인 성공 - 사원번호: {}, 권한: {}", employeeId, role);

        return User.builder()
                .username(employeeId)
                .password(auth.getPassword())
                .authorities(Collections.singletonList(new SimpleGrantedAuthority(role)))
                .build();
    }

    private String getRoleByAuthCode(String authCode) {
        return switch (authCode) {
            case "ATH001" -> "ROLE_GENERAL";      // 일반
            case "ATH002" -> "ROLE_HR";           // 인사
            case "ATH003" -> "ROLE_ADMIN";        // 관리자
            case "ATH004" -> "ROLE_SALES";        // 영업
            case "ATH005" -> "ROLE_FINANCE";      // 재무
            case "ATH006" -> "ROLE_PRODUCTION";   // 생산
            case "ATH007" -> "ROLE_FOREMAN";      // 작업반장
            default -> "ROLE_GENERAL";
        };
    }
} 