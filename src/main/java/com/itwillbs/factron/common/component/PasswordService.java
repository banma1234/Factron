package com.itwillbs.factron.common.component;

import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class PasswordService {

    private final PasswordEncoder passwordEncoder;

    public PasswordService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public String hashPassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    public boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
    
    /**
     * 기본 비밀번호를 생성합니다. (전화번호)
     * @param phone 전화번호
     * @return 암호화된 기본 비밀번호
     */
    public String generateDefaultPassword(String phone) {
        // 전화번호에서 하이픈 제거
        String cleanPhone = phone.split("-")[2];
        log.info("PasswordService phone: " + cleanPhone);
        return hashPassword(cleanPhone);
    }
}
