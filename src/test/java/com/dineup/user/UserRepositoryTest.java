package com.dineup.user;

import com.dineup.AbstractTestcontainers;
import com.dineup.common.util.Role;
import com.dineup.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import com.dineup.user.model.User;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class UserRepositoryTest extends AbstractTestcontainers {

    @Autowired
    private UserRepository underTest;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findByEmail_ShouldReturnUser_WhenEmailExists() {
        // Arrange
        User testUser = new User(
                "John",
                "Doe",
                "johndoe@test.com",
                Role.CUSTOMER,
                "password"
        );

        entityManager.persistAndFlush(testUser);

        // Act
        Optional<User> result = underTest.findByEmail(testUser.getEmail());

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("johndoe@test.com");
    }

    @Test
    void findByEmail_ShouldReturnEmpty_WhenEmailDoesNotExist() {
        // Act & Assert
        Optional<User> result = underTest.findByEmail("johndoe@test.com");

        assertThat(result).isNotPresent();
    }
}
