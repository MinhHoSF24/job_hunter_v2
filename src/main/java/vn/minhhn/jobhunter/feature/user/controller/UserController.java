package vn.minhhn.jobhunter.feature.user.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.minhhn.jobhunter.feature.user.domain.User;
import vn.minhhn.jobhunter.feature.user.dto.CreateUserDto;
import vn.minhhn.jobhunter.feature.user.dto.UpdateUserDto;
import vn.minhhn.jobhunter.feature.user.dto.UserResponseDto;
import vn.minhhn.jobhunter.feature.user.service.UserService;
import vn.minhhn.jobhunter.shared.dto.common.ApiResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping()
    public ResponseEntity<ApiResponse<UserResponseDto>> createUser(@Valid @RequestBody CreateUserDto createDto) {
        UserResponseDto newUser = this.userService.handleCreateUser(createDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Người dùng được tạo thành công", newUser));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> getUserById(@PathVariable("id") Long id) {
        User user = this.userService.handleGetUserById(id);
        logger.info("User retrieved successfully with id: {}", id);
        return ResponseEntity.ok(ApiResponse.success("Lấy thông tin người dùng thành công", user));
    }

    @GetMapping()
    public ResponseEntity<ApiResponse<Page<UserResponseDto>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<UserResponseDto> result = userService.handleGetAllUsers(page, size);
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách người dùng thành công", result));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDto>> updateUser(@PathVariable("id") Long id,
            @Valid @RequestBody UpdateUserDto updatedUserDto) {
        UserResponseDto updatedUserResult = this.userService.handleUpdateUser(id, updatedUserDto);
        logger.info("User updated successfully with id: {}", id);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật thông tin người dùng thành công", updatedUserResult));

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") Long id) {
        this.userService.handleDeleteUser(id);
        logger.info("User deleted successfully with id: {}", id);
        return ResponseEntity.noContent().build();
    }

}
