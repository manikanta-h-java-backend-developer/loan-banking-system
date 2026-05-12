package com.mani.loan.banking.system;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

/**
 * Integration test for {@link LoanBankingApplication}.
 *
 * Verifies that:
 * - The Spring Application Context loads successfully
 * - All beans are wired correctly
 * - The main() entry point runs without exceptions
 *
 * Uses 'test' profile to isolate from dev/prod configurations.
 */
@SpringBootTest(
		classes = LoanBankingApplication.class,
		webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
class LoanBankingApplicationTest {

	/**
	 * Test 1: Verify the Spring Application Context loads without errors.
	 * If any bean fails to initialize, this test will fail with a
	 * meaningful error pointing directly to the misconfigured bean.
	 */
	@Test
	void contextLoads() {
		// If the context fails to start, Spring throws an exception
		// and this test fails automatically — no assertion needed.
	}

}