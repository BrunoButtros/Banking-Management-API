package dev.bruno.banking;

import dev.bruno.banking.security.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BankingManagementApiApplicationTests {

	@Mock
	private JwtTokenProvider jwtTokenProvider;

	@Test
	void contextLoads() {
	}

}
