package dev.bruno.banking.service;

import dev.bruno.banking.dto.ExchangeConversionResponseDTO;
import dev.bruno.banking.exception.ExchangeRateException;
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
class ExchangeRateServiceTest {

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec<?> requestHeadersUriSpecMock;

    @Mock
    private WebClient.RequestHeadersSpec<?> requestHeadersSpecMock;

    @Mock
    private WebClient.ResponseSpec responseSpecMock;

    @InjectMocks
    private ExchangeRateService exchangeRateService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(exchangeRateService, "apiKey", "fakeApiKey");
    }

    @Test
    void testConvertCurrency_ThrowsExchangeRateException_NoApiKey() {
        ReflectionTestUtils.setField(exchangeRateService, "apiKey", "");
        ExchangeRateException ex = assertThrows(ExchangeRateException.class,
                () -> exchangeRateService.convertCurrency("USD", "BRL", 100.0));
        assertTrue(ex.getMessage().contains("EXCHANGERATE_API_KEY is not set."));
    }

    @Test
    void testConvertCurrency_Success() {
        doReturn(requestHeadersUriSpecMock).when(webClient).get();
        doReturn(requestHeadersSpecMock).when(requestHeadersUriSpecMock).uri(anyString());
        doReturn(responseSpecMock).when(requestHeadersSpecMock).retrieve();

        ExchangeConversionResponseDTO mockResponse = new ExchangeConversionResponseDTO();
        mockResponse.setSuccess(true);
        doReturn(Mono.just(mockResponse)).when(responseSpecMock)
                .bodyToMono(ExchangeConversionResponseDTO.class);

        ExchangeConversionResponseDTO result = exchangeRateService.convertCurrency("USD", "BRL", 100.0);

        assertNotNull(result);
        assertTrue(result.isSuccess());
    }

    @Test
    void testConvertCurrency_ThrowsOnWebClientError() {
        doReturn(requestHeadersUriSpecMock).when(webClient).get();
        doReturn(requestHeadersSpecMock).when(requestHeadersUriSpecMock).uri(anyString());
        doReturn(responseSpecMock).when(requestHeadersSpecMock).retrieve();
        doReturn(Mono.error(new RuntimeException("WebClient error")))
                .when(responseSpecMock).bodyToMono(ExchangeConversionResponseDTO.class);

        ExchangeRateException ex = assertThrows(ExchangeRateException.class,
                () -> exchangeRateService.convertCurrency("USD", "BRL", 100.0));
        assertTrue(ex.getMessage().contains("Failed to fetch exchange rate data"));
    }
}
