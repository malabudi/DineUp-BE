package com.dineup.journey;

import com.dineup.AbstractTestcontainers;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
class SmokeIT extends AbstractTestcontainers {

	@Test
	void contextLoads() {}
}
