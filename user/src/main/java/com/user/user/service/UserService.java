package com.user.user.service;

import com.user.user.dto.CreateUserRequest;
import com.user.user.dto.UpdateUserRequest;
import com.user.user.entity.User;
import com.user.user.exception.ResourceNotFoundException;
import com.user.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository repo) {
        this.userRepository = repo;
    }

    public User createUser(CreateUserRequest req) {
        // Optionally check email uniqueness
        userRepository.findByEmail(req.getEmail())
                .ifPresent(u -> { throw new IllegalArgumentException("Email already in use"); });

        User u = User.builder()
                .name(req.getName())
                .email(req.getEmail())
                .phone(req.getPhone())
                .build();
        return userRepository.save(u);
    }

    public User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    public List<User> listUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public User updateUser(Long id, UpdateUserRequest req) {
        User existing = getUser(id);
        existing.setName(req.getName());
        existing.setEmail(req.getEmail());
        existing.setPhone(req.getPhone());
        return userRepository.save(existing);
    }

    public void deleteUser(Long id) {
        User existing = getUser(id);
        userRepository.delete(existing);
    }
}

