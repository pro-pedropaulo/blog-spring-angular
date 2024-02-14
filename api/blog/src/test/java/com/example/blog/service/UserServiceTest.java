package com.example.blog.service;

import com.example.blog.exceptions.ResourceNotFoundException;
import com.example.blog.model.User;
import com.example.blog.repository.UserRepository;
import com.example.blog.util.ExceptionMessages;
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
    void findAll_Empty() {
        when(userRepository.findAll()).thenReturn(Arrays.asList());
        List<User> users = userService.findAll();
        assertTrue(users.isEmpty());
    }

    @Test
    void save() {
        when(userRepository.save(any(User.class))).thenReturn(user);
        User savedUser = userService.save(new User());
        assertNotNull(savedUser);
        assertEquals("testUser", savedUser.getUsername());
    }

    @Test
    void save_notSaved() {
        when(userRepository.save(any(User.class))).thenReturn(null);
        User savedUser = userService.save(new User());
        assertNull(savedUser);
    }
    @Test
    void update() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        User updatedUser = userService.update(1L, user);
        assertTrue(updatedUser.getUsername().equals("testUser"));
        assertEquals("testUser", updatedUser.getUsername());
    }

    @Test
    void update_NotFound() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenThrow(new ResourceNotFoundException(ExceptionMessages.USER_NOT_FOUND + userId));

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> userService.update(userId, new User()));
        assertTrue(exception.getMessage().contains(ExceptionMessages.USER_NOT_FOUND + userId));
    }

    @Test
    void deleteUser_Success() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        doNothing().when(userRepository).delete(user);
        userService.deleteUser(user.getId());
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void deleteUser_NotFound() {
        when(userRepository.findById(1L)).thenThrow(new ResourceNotFoundException(ExceptionMessages.USER_NOT_FOUND + 1L));

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(1L));
        assertTrue(exception.getMessage().contains(ExceptionMessages.USER_NOT_FOUND + 1L));
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
    void findById_NotFound() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenThrow(new ResourceNotFoundException(ExceptionMessages.USER_NOT_FOUND + userId));

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> userService.findById(userId));
        assertTrue(exception.getMessage().contains(ExceptionMessages.USER_NOT_FOUND + userId));
    }

    @Test
    void findByUsername() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        User foundUser = userService.findByUsername("testUser");
        assertNotNull(foundUser);
        assertEquals("testUser", foundUser.getUsername());
    }

    @Test
    void findByUsername_NotFound() {
        when(userRepository.findByUsername("unknownUser")).thenReturn(Optional.empty());
        User foundUser = userService.findByUsername("unknownUser");
        assertNull(foundUser);
    }
    @Test
    void existsByUsername() {
        when(userRepository.existsByUsername("testUser")).thenReturn(true);
        boolean exists = userService.existsByUsername("testUser");
        assertTrue(exists);
    }
}
