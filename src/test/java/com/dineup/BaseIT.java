package com.dineup;

import com.dineup.common.config.JwtService;
import com.dineup.common.util.Role;
import com.dineup.user.model.User;
import com.dineup.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.client.RestTestClient;

@Component
public abstract class BaseIT extends AbstractTestcontainers {

    @Autowired
    protected JwtService jwtService;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected RestTestClient restTestClient;

    protected String adminToken;
    protected String customerToken;

    protected void setUpTestUsers() {
        userRepository.deleteAll(); // Clean slate

        User admin = new User("Test", "Admin", "admin@test.com", Role.ADMIN, "password");
        userRepository.save(admin);
        adminToken = jwtService.generateToken(admin);

        User customer = new User("Test", "Customer", "cust@test.com", Role.CUSTOMER, "password");
        userRepository.save(customer);
        customerToken = jwtService.generateToken(customer);
    }
}
