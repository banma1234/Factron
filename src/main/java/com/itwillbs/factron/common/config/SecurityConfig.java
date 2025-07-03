package com.itwillbs.factron.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itwillbs.factron.entity.Employee;
import com.itwillbs.factron.repository.employee.EmployeeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.thymeleaf.extras.springsecurity6.dialect.SpringSecurityDialect;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@Log4j2
@RequiredArgsConstructor
public class SecurityConfig {

    private final EmployeeRepository employeeRepository;
    private final ObjectMapper objectMapper;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/", "/**.ico", "/login", "/css/**", "/js/**", "/images/**", "/api/sys/**").permitAll()
//                .requestMatchers("/api/employee").permitAll()
                .requestMatchers("/api/**").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .usernameParameter("employeeId")
                .passwordParameter("password")
                .successHandler(authenticationSuccessHandler())
                .failureHandler(authenticationFailureHandler())
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID", "EMPLOYEE_INFO")
                .permitAll()
            )
            .sessionManagement(session -> session
                .maximumSessions(1)
                .expiredUrl("/login?expired=true")
                .maxSessionsPreventsLogin(false)
            )
            .csrf(csrf -> csrf.disable());

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, 
                                              HttpServletResponse response, 
                                              Authentication authentication) throws IOException, ServletException {
                
                // 세션에 사용자 정보 저장
                HttpSession session = request.getSession();
                session.setAttribute("employeeId", authentication.getName());
                session.setAttribute("authorities", authentication.getAuthorities());
                
                // 사용자 상세 정보 조회
                Employee employee = employeeRepository.findById(Long.parseLong(authentication.getName()))
                        .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));
                
                session.setAttribute("employeeName", employee.getName());
                
                // 쿠키에 사용자 정보 저장
                setUserInfoCookie(response, employee, authentication);
                
                log.debug("로그인 성공 - 사용자: {}, 이름: {}, 권한: {}",
                    authentication.getName(), 
                    employee.getName(),
                    authentication.getAuthorities());
                
                response.sendRedirect("/employee");
            }
        };
    }

    /**
     * 사용자 정보를 쿠키에 저장
     */
    private void setUserInfoCookie(HttpServletResponse response, Employee employee, Authentication authentication) {
        try {
            log.debug("쿠키 설정 시작 - 사용자: {}", employee.getId());
            
            // 사용자 정보를 JSON 형태로 직렬화
            Map<String, String> userInfo = new HashMap<>();
            userInfo.put("employeeId", employee.getId().toString());
            userInfo.put("employeeName", employee.getName());
            userInfo.put("authorities", authentication.getAuthorities().stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(",")));
            
            log.debug("사용자 정보 맵 생성 완료: {}", userInfo);
            
            String userInfoJson = objectMapper.writeValueAsString(userInfo);
            log.debug("JSON 직렬화 완료: {}", userInfoJson);
            
            // JSON을 Base64로 인코딩
            String encodedUserInfo = Base64.getEncoder().encodeToString(userInfoJson.getBytes("UTF-8"));
            log.debug("Base64 인코딩 완료: {}", encodedUserInfo);
            
            // 쿠키 생성 및 설정
            Cookie userInfoCookie = new Cookie("EMPLOYEE_INFO", encodedUserInfo);
            userInfoCookie.setPath("/");
            userInfoCookie.setMaxAge(24 * 60 * 60); // 24시간
            userInfoCookie.setHttpOnly(false); // JavaScript에서 접근 가능하도록
            userInfoCookie.setSecure(false); // HTTPS가 아닌 경우 false
            
            response.addCookie(userInfoCookie);
            
            log.debug("사용자 정보 쿠키 설정 완료: {}", userInfoJson);
            
        } catch (Exception e) {
            log.error("쿠키 설정 실패 - 상세 에러: ", e);
            e.printStackTrace();
        }
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return new AuthenticationFailureHandler() {
            @Override
            public void onAuthenticationFailure(HttpServletRequest request, 
                                              HttpServletResponse response, 
                                              AuthenticationException exception) throws IOException, ServletException {
                String employeeId = request.getParameter("employeeId");
                
                log.warn("로그인 실패 - 사원번호: {}, 에러: {}", 
                    employeeId, 
                    exception.getMessage());
                
                response.sendRedirect("/login?error=true");
            }
        };
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Thymeleaf Security Dialect 설정
     */
    @Bean
    public SpringSecurityDialect springSecurityDialect() {
        return new SpringSecurityDialect();
    }
} 