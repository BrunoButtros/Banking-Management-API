package dev.bruno.banking.service;

import dev.bruno.banking.dto.BalanceResponseDTO;
import dev.bruno.banking.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.doReturn;

@ExtendWith(MockitoExtension.class)
class MockBalanceServiceTest {

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec<?> requestHeadersUriSpecMock;

    @Mock
    private WebClient.RequestHeadersSpec<?> requestHeadersSpecMock;

    @Mock
    private WebClient.ResponseSpec responseSpecMock;

    @InjectMocks
    private MockBalanceService mockBalanceService;

    @BeforeEach
    void setUp() {
        doReturn(webClient).when(webClientBuilder).build();
        ReflectionTestUtils.setField(mockBalanceService, "mockApiUrl", "http://test-mock-url");
    }

    @Test
    void testGetBalance_Success() {
        BalanceResponseDTO dto = new BalanceResponseDTO();
        dto.setBalance(500.0);
        dto.setCurrency("USD");

        doReturn(requestHeadersUriSpecMock).when(webClient).get();
        doReturn(requestHeadersSpecMock).when(requestHeadersUriSpecMock).uri(anyString());
        doReturn(responseSpecMock).when(requestHeadersSpecMock).retrieve();
        doReturn(Mono.just(dto)).when(responseSpecMock).bodyToMono(BalanceResponseDTO.class);

        BalanceResponseDTO result = mockBalanceService.getBalance();

        assertNotNull(result);
        assertEquals(500.0, result.getBalance());
        assertEquals("USD", result.getCurrency());
    }

    @Test
    void testGetBalance_ThrowsBusinessException() {
        doReturn(requestHeadersUriSpecMock).when(webClient).get();
        doReturn(requestHeadersSpecMock).when(requestHeadersUriSpecMock).uri(anyString());
        doReturn(responseSpecMock).when(requestHeadersSpecMock).retrieve();
        doReturn(Mono.error(new RuntimeException("Error")))
                .when(responseSpecMock).bodyToMono(BalanceResponseDTO.class);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> mockBalanceService.getBalance());
        assertTrue(ex.getMessage().contains("Failed to retrieve mock balance"));
    }
}
