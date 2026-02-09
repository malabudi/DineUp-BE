package com.malabudi.dineupbe.menu.repository;

import com.malabudi.dineupbe.AbstractTestcontainers;
import com.malabudi.dineupbe.menu.model.MenuGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class MenuGroupRepositoryTest extends AbstractTestcontainers {

    @Autowired
    private MenuGroupRepository underTest;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void setup() {
        underTest.deleteAll();
    }

    @Test
    void itShouldSaveAndFindMenuGroupById() {
        // Arrange
        MenuGroup menuGroup = new MenuGroup("Appetizers");

        // Act
        MenuGroup savedMenuGroup = underTest.save(menuGroup);

        // Assert
        assertThat(underTest.findById(savedMenuGroup.getId())).isPresent();
        assertThat(savedMenuGroup.getName()).isEqualTo("Appetizers");
    }
}
