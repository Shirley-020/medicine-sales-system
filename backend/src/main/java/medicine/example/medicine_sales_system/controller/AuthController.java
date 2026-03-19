package medicine.example.medicine_sales_system.controller;

import medicine.example.medicine_sales_system.dto.AuthRequest;
import medicine.example.medicine_sales_system.dto.AuthResponse;
import medicine.example.medicine_sales_system.entity.User;
import medicine.example.medicine_sales_system.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import medicine.example.medicine_sales_system.common.ApiResponse;
import java.util.List;
import java.util.stream.Collectors;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final org.springframework.security.authentication.AuthenticationManager authenticationManager;
    private final medicine.example.medicine_sales_system.security.JwtTokenUtil jwtTokenUtil;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder,
                          org.springframework.security.authentication.AuthenticationManager authenticationManager,
                          medicine.example.medicine_sales_system.security.JwtTokenUtil jwtTokenUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest req) {
        try {
            if (req == null || req.getUsername() == null || req.getPassword() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new AuthResponse(false, "username and password required"));
            }
            Optional<User> ou = userRepository.findByUsername(req.getUsername());
            if (ou.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse(false, "Invalid credentials"));
            }
            User u = ou.get();
            if (u.getPasswordHash() == null) {
                log.warn("User {} has null password_hash", u.getUsername());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse(false, "Invalid credentials"));
            }

            // 密码对比处新增日志
            if (!passwordEncoder.matches(req.getPassword(), u.getPasswordHash())) {
                log.error("密码对比失败！前端传递的明文密码：{}，数据库存储的hash：{}", req.getPassword(), u.getPasswordHash());
                log.error("加密对比结果：{}", passwordEncoder.matches(req.getPassword(), u.getPasswordHash()));
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse(false, "Invalid credentials"));
            }   
            else {
                log.info("密码对比成功！用户：{}", u.getUsername());
            }
            if (!passwordEncoder.matches(req.getPassword(), u.getPasswordHash())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse(false, "Invalid credentials"));
            }
            

            // Authenticate via AuthenticationManager to integrate with Spring Security
            org.springframework.security.authentication.UsernamePasswordAuthenticationToken authToken =
                    new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword());
            org.springframework.security.core.Authentication auth = authenticationManager.authenticate(authToken);

            String jwt = jwtTokenUtil.generateToken(auth);

            AuthResponse resp = new AuthResponse(true, "Login successful");
            resp.setUsername(u.getUsername());
            resp.setRole(u.getRole() != null ? u.getRole().getRoleName() : null);
            resp.setToken(jwt);
            return ResponseEntity.ok(resp);
        } catch (Exception ex) {
            log.error("Auth login failed for user {}: {}", req != null ? req.getUsername() : "<null>", ex.toString(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new AuthResponse(false, "Internal error: " + ex.getClass().getSimpleName()));
        }
    }
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<String>>> getUsers() {
        try {
            List<String> usernames = userRepository.findAll().stream()
                .filter(user -> user.getStatus() != null && user.getStatus() == 1) // 只返回启用状态的用户
                .map(User::getUsername)
                .filter(username -> username != null && !username.trim().isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
            return ResponseEntity.ok(ApiResponse.success(usernames));
        } catch (Exception ex) {
            log.error("获取用户列表失败: {}", ex.toString(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("获取用户列表失败: " + ex.getMessage()));
        }
    }
    
    public static void main(String[] args) {
        // 用和项目中一致的BCrypt编码器生成哈希
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String correctHash = encoder.encode("123456");
        System.out.println("123456的正确BCrypt哈希：" + correctHash);
        // 验证匹配结果（必须输出true）
        boolean match = encoder.matches("123456", correctHash);
        System.out.println("匹配结果：" + match);
    }
}

