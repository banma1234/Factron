package com.itwillbs.factron.controller.login;

import com.itwillbs.factron.dto.ResponseDTO;
import com.itwillbs.factron.dto.login.Login;
import com.itwillbs.factron.service.login.LoginService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Controller
@RequiredArgsConstructor
public class LoginRestController {

    // private final LoginService loginService;

    @PostMapping("/api/auth/login")
    public ResponseDTO<Void> login(@RequestBody Login login) {
        log.info("Login request received: {}", login);

        return ResponseDTO.success("성공", null);
    }
}
