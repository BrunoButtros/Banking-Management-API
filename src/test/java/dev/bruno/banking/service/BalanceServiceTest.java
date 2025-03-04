package dev.bruno.banking.service;

import dev.bruno.banking.dto.BalanceResponseDTO;
import dev.bruno.banking.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BalanceServiceTest {

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec<?> requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec<?> requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private BalanceService balanceService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(balanceService, "balanceUrl", "http://test-url");
    }

    @Test
    void testGetBalances_Success() {
        BalanceResponseDTO dto = new BalanceResponseDTO();
        dto.setBalance(123.45);
        dto.setCurrency("USD");
        List<BalanceResponseDTO> mockResponse = Collections.singletonList(dto);

        ParameterizedTypeReference<List<BalanceResponseDTO>> typeRef = new ParameterizedTypeReference<>() {};

        doReturn(requestHeadersUriSpec).when(webClient).get();
        doReturn(requestHeadersSpec).when(requestHeadersUriSpec).uri("http://test-url");
        doReturn(responseSpec).when(requestHeadersSpec).retrieve();
        doReturn(Mono.just(mockResponse)).when(responseSpec).bodyToMono(typeRef);

        List<BalanceResponseDTO> result = balanceService.getBalances("user@example.com");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(123.45, result.get(0).getBalance());
        verify(webClient).get();
    }

    @Test
    void testGetBalances_ThrowsBusinessException() {
        ParameterizedTypeReference<List<BalanceResponseDTO>> typeRef = new ParameterizedTypeReference<>() {};

        doReturn(requestHeadersUriSpec).when(webClient).get();
        doReturn(requestHeadersSpec).when(requestHeadersUriSpec).uri("http://test-url");
        doReturn(responseSpec).when(requestHeadersSpec).retrieve();
        doReturn(Mono.error(new RuntimeException("WebClient error"))).when(responseSpec).bodyToMono(typeRef);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> balanceService.getBalances("user@example.com"));

        assertTrue(ex.getMessage().contains("Failed to retrieve balance data for user: user@example.com"));
    }
}
