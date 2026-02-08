package com.malabudi.dineupbe.menuitem;

import com.malabudi.dineupbe.AbstractTestcontainers;
import com.malabudi.dineupbe.menu.model.MenuGroup;
import com.malabudi.dineupbe.menu.model.MenuItem;
import com.malabudi.dineupbe.menu.repository.MenuItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class MenuItemRepositoryTest extends AbstractTestcontainers {

    @Autowired
    private MenuItemRepository underTest;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void setup() {
        underTest.deleteAll();
    }

    @Test
    void itShouldSaveAndFindMenuItemById() {
        // Arrange
        MenuGroup menuGroup = new MenuGroup("Main Course");
        entityManager.persist(menuGroup);

        MenuItem menuItem = new MenuItem(
                "Hamburger",
                "Tasty burger",
                new BigDecimal("8.99"),
                ""
        );
        menuItem.setMenuGroup(menuGroup);

        // Act
        MenuItem savedMenuItem = underTest.save(menuItem);

        // Assert
        assertThat(underTest.findById(savedMenuItem.getId())).isPresent();
        assertThat(savedMenuItem.getName()).isEqualTo("Hamburger");
        assertThat(savedMenuItem.getDescription()).isEqualTo("Tasty burger");
        assertThat(savedMenuItem.getPrice()).isEqualTo(new BigDecimal("8.99"));
    }
}
