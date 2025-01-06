package com.dayone.web;

import com.dayone.model.constants.Auth;
import com.dayone.security.TokenProvider;
import com.dayone.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final MemberService memberService;
    private final TokenProvider tokenProvider;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody Auth.SignUp request) {
        var result = this.memberService.register(request);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody Auth.SignIn request) {
        // 아이디, 패스워드 일치 여부 확인
        var member = this.memberService.authenticate(request);

        // 토큰 생성
        var token = this.tokenProvider.generateToken(member.getUsername(), member.getRoles());

        log.info("user login : " + request.getUsername());
        return ResponseEntity.ok(token);
    }
}
