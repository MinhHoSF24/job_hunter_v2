package vn.minhhn.jobhunter.feature.auth.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import vn.minhhn.jobhunter.feature.auth.domain.RefreshToken;
import vn.minhhn.jobhunter.feature.auth.dto.LoginRequest;
import vn.minhhn.jobhunter.feature.auth.dto.LoginResponse;
import vn.minhhn.jobhunter.feature.auth.dto.RegisterMapper;
import vn.minhhn.jobhunter.feature.auth.dto.RegisterRequest;
import vn.minhhn.jobhunter.feature.auth.dto.RegisterResponse;
import vn.minhhn.jobhunter.feature.auth.repository.RefreshTokenRepository;
import vn.minhhn.jobhunter.feature.user.domain.User;
import vn.minhhn.jobhunter.feature.user.dto.UserResponseDto;
import vn.minhhn.jobhunter.feature.user.repository.UserRepository;
import vn.minhhn.jobhunter.shared.exception.DuplicateResourceException;
import vn.minhhn.jobhunter.shared.exception.InvalidTokenException;
import vn.minhhn.jobhunter.shared.exception.ResourceNotFoundException;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final RegisterMapper registerMapper;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-expiration}")
    private long refreshTokenExpiration;

    public AuthService(AuthenticationManager authenticationManager, JwtEncoder jwtEncoder,
            UserRepository userRepository, RefreshTokenRepository refreshTokenRepository,
            PasswordEncoder passwordEncoder, RegisterMapper registerMapper) {
        this.authenticationManager = authenticationManager;
        this.jwtEncoder = jwtEncoder;
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.registerMapper = registerMapper;
    }

    @Transactional
    public LoginResponse login(LoginRequest request, String deviceInfo, String ipAddress) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng", "email", request.getEmail()));

        String rawAccessToken = generateAccessToken(authentication, user.getId());
        String rawRefreshToken = generateRefreshToken(user.getId(), user.getEmail());

        saveRefreshToken(rawRefreshToken, user, deviceInfo, ipAddress);

        log.info("Người dùng đăng nhập thành công: {}", user.getEmail());
        return new LoginResponse(rawAccessToken, rawRefreshToken);
    }

    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Người dùng", "email", request.email());
        }
        User user = registerMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.password()));
        User saved = userRepository.save(user);
        log.info("Người dùng đăng ký thành công: {}", saved.getEmail());
        return registerMapper.toResponseDto(saved);
    }

    private String generateAccessToken(Authentication authentication, Long userId) {
        Instant now = Instant.now();

        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(authentication.getName())
                .issuedAt(now)
                .expiresAt(now.plusMillis(accessTokenExpiration))
                .claim("userId", userId)
                .claim("roles", roles)
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    @Transactional
    public LoginResponse refresh(String rawRefreshToken) {
        String tokenHash = hashToken(rawRefreshToken);
        RefreshToken storedToken = refreshTokenRepository.findByToken(tokenHash)
                .orElseThrow(() -> new InvalidTokenException("Refresh token không hợp lệ"));

        if (storedToken.isRevoked() || storedToken.getExpiresAt().isBefore(Instant.now())) {
            throw new InvalidTokenException("Refresh token đã hết hạn hoặc bị thu hồi");
        }
        storedToken.setRevoked(true);
        refreshTokenRepository.save(storedToken);

        User user = storedToken.getUser();
        List<String> roles = user.getRoles().stream()
                .map(role -> "ROLE_" + role.getName())
                .toList();
        String newAccessToken = generateAccessTokenFromUser(user.getId(), user.getEmail(), roles);
        String newRefreshToken = generateRefreshToken(user.getId(), user.getEmail());

        saveRefreshToken(newRefreshToken, user, storedToken.getDeviceInfo(), storedToken.getIpAddress());

        log.info("Refresh token được làm mới cho người dùng: {}", user.getEmail());
        return new LoginResponse(newAccessToken, newRefreshToken);
    }

    @Transactional
    public void logout(String rawRefreshToken) {
        String tokenHash = hashToken(rawRefreshToken);

        RefreshToken storedToken = refreshTokenRepository.findByToken(tokenHash)
                .orElseThrow(() -> new InvalidTokenException("Refresh token không hợp lệ"));

        storedToken.setRevoked(true);
        refreshTokenRepository.save(storedToken);
        log.info("Người dùng đăng xuất thành công: {}", storedToken.getUser().getEmail());
    }

    // @Transactional(readOnly = true)
    // public UserResponseDto getMe(String email) {
    // User user = userRepository.findByEmail(email)
    // .orElseThrow(() -> new ResourceNotFoundException("Người dùng", "email",
    // email));
    // return UserResponseDto.fromEntity(user);
    // }

    private String generateAccessTokenFromUser(Long userId, String email, List<String> roles) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(email)
                .issuedAt(now)
                .expiresAt(now.plusMillis(accessTokenExpiration))
                .claim("userId", userId)
                .claim("roles", roles)
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    private String generateRefreshToken(Long userId, String email) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .id(java.util.UUID.randomUUID().toString())
                .subject(email)
                .issuedAt(now)
                .expiresAt(now.plusMillis(refreshTokenExpiration))
                .claim("userId", userId)
                .claim("type", "refresh")
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    private void saveRefreshToken(String rawToken, User user, String deviceInfo, String ipAddress) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(hashToken(rawToken));
        refreshToken.setUser(user);
        refreshToken.setExpiresAt(Instant.now().plusMillis(refreshTokenExpiration));
        refreshToken.setDeviceInfo(deviceInfo);
        refreshToken.setIpAddress(ipAddress);
        refreshToken.setRevoked(false);
        refreshTokenRepository.save(refreshToken);
    }

    private String hashToken(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 không khả dụng", e);
        }
    }

}
