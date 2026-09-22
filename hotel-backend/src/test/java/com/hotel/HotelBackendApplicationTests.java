package com.hotel;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@EnabledIfEnvironmentVariable(named = "DB_INTEGRATION_TEST", matches = "true")
class HotelBackendApplicationTests {

	@Test
	void contextLoads() {
	}

}

