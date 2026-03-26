package vn.minhhn.jobhunter.feature.auth.controller;

import java.util.Arrays;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import vn.minhhn.jobhunter.feature.auth.dto.LoginRequest;
import vn.minhhn.jobhunter.feature.auth.dto.LoginResponse;
import vn.minhhn.jobhunter.feature.auth.dto.RefreshRequest;
import vn.minhhn.jobhunter.feature.auth.dto.RegisterRequest;
import vn.minhhn.jobhunter.feature.auth.dto.RegisterResponse;
import vn.minhhn.jobhunter.feature.auth.service.AuthService;
import vn.minhhn.jobhunter.shared.dto.common.ApiResponse;
import vn.minhhn.jobhunter.shared.exception.InvalidTokenException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";
    private static final String COOKIE_PATH = "/api/auth";
    private static final int COOKIE_MAX_AGE = 259200;
    private final AuthService authService;

    public AuthController(AuthenticationManager authenticationManager, AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {
        String deviceInfo = httpRequest.getHeader("User-Agent");
        String ipAddress = extractClientIp(httpRequest);

        LoginResponse loginResponse = authService.login(loginRequest, deviceInfo, ipAddress);
        setRefreshTokenCookie(httpResponse, loginResponse.getRefreshToken());

        return ResponseEntity.ok(ApiResponse.success("Đăng nhập thành công", loginResponse));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(
            @Valid @RequestBody RegisterRequest request) {

        RegisterResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Đăng ký thành công", response));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refresh(
            @RequestBody(required = false) RefreshRequest body,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {

        String rawRefreshToken = extractRefreshToken(body, httpRequest);
        if (rawRefreshToken == null) {
            throw new InvalidTokenException("Refresh token không được cung cấp");
        }

        LoginResponse loginResponse = authService.refresh(rawRefreshToken);
        setRefreshTokenCookie(httpResponse, loginResponse.getRefreshToken());

        return ResponseEntity.ok(ApiResponse.success("Token đã được làm mới", loginResponse));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {

        String rawRefreshToken = extractRefreshTokenFromCookie(httpRequest);
        if (rawRefreshToken != null) {
            authService.logout(rawRefreshToken);
        }
        clearRefreshTokenCookie(httpResponse);

        return ResponseEntity.ok(ApiResponse.success("Đăng xuất thành công", null));
    }

    private String extractRefreshToken(RefreshRequest body, HttpServletRequest request) {
        String fromCookie = extractRefreshTokenFromCookie(request);
        if (fromCookie != null) {
            return fromCookie;
        }
        if (body != null && body.refreshToken() != null && !body.refreshToken().isBlank()) {
            return body.refreshToken();
        }
        return null;
    }

    private String extractRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }
        return Arrays.stream(request.getCookies())
                .filter(cookie -> REFRESH_TOKEN_COOKIE_NAME.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    private void setRefreshTokenCookie(HttpServletResponse response, String rawToken) {
        response.setHeader("Set-Cookie",
                REFRESH_TOKEN_COOKIE_NAME + "=" + rawToken
                        + "; HttpOnly; Secure; SameSite=Lax; Path=" + COOKIE_PATH
                        + "; Max-Age=" + COOKIE_MAX_AGE);
    }

    private void clearRefreshTokenCookie(HttpServletResponse response) {
        response.setHeader("Set-Cookie", REFRESH_TOKEN_COOKIE_NAME + "=; Max-Age=0; Path=" + COOKIE_PATH);
    }

    private String extractClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

}
