package com.example.blog.service;

import com.example.blog.model.User;
import com.example.blog.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        user.setPassword("testPass");
        user.setEmail("test@example.com");
        user.setGender("Non-Binary");
    }

    @Test
    void findAll() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(user));
        List<User> users = userService.findAll();
        assertFalse(users.isEmpty());
        assertEquals(1, users.size());
        assertEquals("testUser", users.get(0).getUsername());
    }

    @Test
    void save() {
        when(userRepository.save(any(User.class))).thenReturn(user);
        User savedUser = userService.save(new User());
        assertNotNull(savedUser);
        assertEquals("testUser", savedUser.getUsername());
    }

    @Test
    void update() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        Optional<User> updatedUser = userService.update(1L, user);
        assertTrue(updatedUser.isPresent());
        assertEquals("testUser", updatedUser.get().getUsername());
    }

    @Test
    void delete() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        boolean deleted = userService.delete(1L);
        assertTrue(deleted);
        verify(userRepository).delete(user);
    }

    @Test
    void authenticate_Success() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        User authenticatedUser = userService.authenticate("testUser", "testPass");
        assertNotNull(authenticatedUser);
        assertEquals("testUser", authenticatedUser.getUsername());
    }

    @Test
    void authenticate_Failure() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        User authenticatedUser = userService.authenticate("testUser", "wrongPass");
        assertNull(authenticatedUser);
    }

    @Test
    void findById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        User foundUser = userService.findById(1L);
        assertNotNull(foundUser);
        assertEquals("testUser", foundUser.getUsername());
    }

    @Test
    void findByUsername() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        User foundUser = userService.findByUsername("testUser");
        assertNotNull(foundUser);
        assertEquals("testUser", foundUser.getUsername());
    }

    @Test
    void existsByUsername() {
        when(userRepository.existsByUsername("testUser")).thenReturn(true);
        boolean exists = userService.existsByUsername("testUser");
        assertTrue(exists);
    }
}
