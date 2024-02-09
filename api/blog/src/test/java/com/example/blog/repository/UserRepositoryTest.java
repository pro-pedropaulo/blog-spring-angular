package com.example.blog.repository;

import com.example.blog.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setUsername("user1");
        user1.setPassword("password1");
        user1.setEmail("user1@example.com");
        user1.setGender("Male");
        entityManager.persist(user1);

        user2 = new User();
        user2.setUsername("user2");
        user2.setPassword("password2");
        user2.setEmail("user2@example.com");
        user2.setGender("Female");
        entityManager.persist(user2);

        entityManager.flush();
    }

    @Test
    void findByUsername() {
        Optional<User> foundUser = userRepository.findByUsername(user1.getUsername());
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo(user1.getUsername());
        assertThat(foundUser.get().getEmail()).isEqualTo(user1.getEmail());
    }

    @Test
    void existsByUsername() {
        boolean exists = userRepository.existsByUsername(user1.getUsername());
        assertThat(exists).isTrue();

        boolean notExists = userRepository.existsByUsername("nonExistingUser");
        assertThat(notExists).isFalse();
    }
}
