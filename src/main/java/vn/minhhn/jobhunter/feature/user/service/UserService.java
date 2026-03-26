package vn.minhhn.jobhunter.feature.user.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;
import vn.minhhn.jobhunter.feature.company.domain.Company;
import vn.minhhn.jobhunter.feature.company.repository.CompanyRepository;
import vn.minhhn.jobhunter.feature.role.domain.Role;
import vn.minhhn.jobhunter.feature.role.dto.RoleResponse;
import vn.minhhn.jobhunter.feature.role.repository.RoleRepository;
import vn.minhhn.jobhunter.feature.user.domain.User;
import vn.minhhn.jobhunter.feature.user.dto.CreateUserDto;
import vn.minhhn.jobhunter.feature.user.dto.UpdateUserDto;
import vn.minhhn.jobhunter.feature.user.dto.UserMapper;
import vn.minhhn.jobhunter.feature.user.dto.UserResponseDto;
import vn.minhhn.jobhunter.feature.user.repository.UserRepository;
import vn.minhhn.jobhunter.shared.exception.DuplicateResourceException;
import vn.minhhn.jobhunter.shared.exception.ResourceNotFoundException;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final RoleRepository roleRepository;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final ObjectMapper objectMapper;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, ObjectMapper objectMapper, UserMapper userMapper,
            PasswordEncoder passwordEncoder, CompanyRepository companyRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.companyRepository = companyRepository;
        this.roleRepository = roleRepository;
    }

    @Transactional
    public UserResponseDto handleCreateUser(CreateUserDto userDto) {
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new DuplicateResourceException("Người dùng", "email", userDto.getEmail());
        }

        Company company = resolveCompany(userDto.getCompanyId());
        List<Role> roles = resolveRoles(userDto.getRoleIds());

        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        User user = this.userMapper.toEntity(userDto);
        user.setCompany(company);
        user.setRoles(roles);
        User savedUser = this.userRepository.save(user);
        return this.userMapper.toResponseDto(savedUser);
    }

    public User handleGetUserById(Long id) {
        Optional<User> userOpt = this.userRepository.findById(id);
        if (userOpt.isPresent()) {
            return userOpt.get();
        } else {
            throw new ResourceNotFoundException("User", "id", id);
        }
    }

    public Page<UserResponseDto> handleGetAllUsers(int page, int size) {
      return userRepository.findAll(PageRequest.of(page, size))
                .map(userMapper::toResponseDto);
    }

    @Transactional
    public UserResponseDto handleUpdateUser(Long id, UpdateUserDto updatedUserDto) {
        if (userRepository.existsByEmailAndIdNot(updatedUserDto.getEmail(), id)) {
            throw new DuplicateResourceException("Người dùng", "email", updatedUserDto.getEmail());
        }
        User existingUser = this.handleGetUserById(id);
        Company company = resolveCompany(updatedUserDto.getCompanyId());
        List<Role> roles = resolveRoles(updatedUserDto.getRoleIds());
        userMapper.updateEntityFromDto(updatedUserDto, existingUser);

        existingUser.setCompany(company);
        existingUser.setRoles(roles);

        User savedUser = this.userRepository.save(existingUser);
        return this.userMapper.toResponseDto(savedUser);
    }

    @Transactional
    public void handleDeleteUser(Long id) {
        User existingUser = this.handleGetUserById(id);
        if (existingUser != null) {
            this.userRepository.deleteById(id);
        }
    }

    private Company resolveCompany(Long companyId) {
        if (companyId == null) {
            return null;
        }
        return companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company", "id", companyId));
    }

    private List<Role> resolveRoles(List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> uniqueIds = roleIds.stream().distinct().toList();
        List<Role> found = roleRepository.findAllById(uniqueIds);

        if (found.size() != uniqueIds.size()) {
            Set<Long> foundIds = found.stream().map(Role::getId).collect(Collectors.toSet());
            Long missingIds = uniqueIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .findFirst()
                    .orElseThrow();
            throw new ResourceNotFoundException("Role", "id", missingIds);
        }

        return found;
    }
}
