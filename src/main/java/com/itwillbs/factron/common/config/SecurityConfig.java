package com.itwillbs.factron.common.config;

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

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@Configuration
@EnableWebSecurity
@Log4j2
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/", "/login", "/css/**", "/js/**", "/images/**", "/api/sys/**").permitAll()
                .requestMatchers("/api/employee").permitAll()
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
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .sessionManagement(session -> session
                .maximumSessions(1) // 동일 사용자 최대 1개 세션만 허용
                .expiredUrl("/login?expired=true")
                .maxSessionsPreventsLogin(false) // 기존 세션 무효화
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
                
                log.info("로그인 성공 - 사용자: {}, 권한: {}, 세션ID: {}", 
                    authentication.getName(), 
                    authentication.getAuthorities(),
                    session.getId());
                
                response.sendRedirect("/employee");
            }
        };
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
} 