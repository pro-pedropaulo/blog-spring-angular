package com.example.blog.controller;

import com.example.blog.DTO.LoginDTO;
import com.example.blog.model.User;
import com.example.blog.security.JWTUtil;
import com.example.blog.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private JWTUtil jwtUtil;

    @InjectMocks
    private UserController userController;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        user.setPassword("password");
        user.setEmail("test@example.com");
    }

    @Test
    void getAllUsers() {
        when(userService.findAll()).thenReturn(Arrays.asList(user));
        List<User> result = userController.getAllUsers();
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(user.getUsername(), result.get(0).getUsername());
    }

    @Test
    void login_Success() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("testUser");
        loginDTO.setPassword("password");

        when(userService.authenticate(loginDTO.getUsername(), loginDTO.getPassword())).thenReturn(user);
        when(jwtUtil.generateToken(user.getUsername())).thenReturn("token");

        ResponseEntity<?> response = userController.login(loginDTO);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(((HashMap) response.getBody()).containsKey("token"));
        assertTrue(((HashMap) response.getBody()).containsKey("username"));
    }

    @Test
    void login_Failure() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("testUser");
        loginDTO.setPassword("wrongPassword");

        when(userService.authenticate(loginDTO.getUsername(), loginDTO.getPassword())).thenReturn(null);

        ResponseEntity<?> response = userController.login(loginDTO);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void createUser_UsernameExists() {
        when(userService.existsByUsername(user.getUsername())).thenReturn(true);

        ResponseEntity<?> response = userController.createUser(user);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Error: Username is already taken!", response.getBody());
    }

    @Test
    void createUser_Success() {
        when(userService.existsByUsername(user.getUsername())).thenReturn(false);
        when(userService.save(any(User.class))).thenReturn(user);

        ResponseEntity<?> response = userController.createUser(user);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
    }

    @Test
    void updateUser_Success() {
        when(userService.update(any(Long.class), any(User.class))).thenReturn(Optional.of(user));

        ResponseEntity<User> response = userController.updateUser(user.getId(), user);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
    }

    @Test
    void updateUser_NotFound() {
        when(userService.update(any(Long.class), any(User.class))).thenReturn(Optional.empty());

        ResponseEntity<User> response = userController.updateUser(user.getId(), user);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void deleteUser_Success() {
        when(userService.delete(user.getId())).thenReturn(true);

        ResponseEntity<?> response = userController.deleteUser(user.getId());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void deleteUser_NotFound() {
        when(userService.delete(user.getId())).thenReturn(false);

        ResponseEntity<?> response = userController.deleteUser(user.getId());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
