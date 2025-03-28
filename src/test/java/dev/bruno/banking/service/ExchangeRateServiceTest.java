package dev.bruno.banking.service;

import dev.bruno.banking.dto.ExchangeConversionResponseDTO;
import dev.bruno.banking.dto.ExchangeConversionResponseDTO.Info;
import dev.bruno.banking.dto.ExchangeConversionResponseDTO.Query;
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
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.any;
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
    void testConvertCurrency_NoApiKey() {
        ReflectionTestUtils.setField(exchangeRateService, "apiKey", "");
        ExchangeRateException ex = assertThrows(ExchangeRateException.class, () ->
                exchangeRateService.convertCurrency("USD", "BRL", 100.0));
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
    void testConvertCurrency_WebClientError() {
        doReturn(requestHeadersUriSpecMock).when(webClient).get();
        doReturn(requestHeadersSpecMock).when(requestHeadersUriSpecMock).uri(anyString());
        doReturn(responseSpecMock).when(requestHeadersSpecMock).retrieve();
        doReturn(Mono.error(new RuntimeException("WebClient error")))
                .when(responseSpecMock).bodyToMono(ExchangeConversionResponseDTO.class);
        ExchangeRateException ex = assertThrows(ExchangeRateException.class, () ->
                exchangeRateService.convertCurrency("USD", "BRL", 100.0));
        assertTrue(ex.getMessage().contains("Failed to fetch exchange rate data"));
    }

    @Test
    void testConvertAndFormat_Failure_NullDTO() {
        ExchangeRateService spyService = Mockito.spy(exchangeRateService);
        doReturn(null).when(spyService).convertCurrency(anyString(), anyString(), anyDouble());
        String result = spyService.convertAndFormat("USD", "BRL", 100.0);
        assertEquals("Conversion failed.", result);
    }

    @Test
    void testConvertAndFormat_Failure_NotSuccess() {
        ExchangeRateService spyService = Mockito.spy(exchangeRateService);
        ExchangeConversionResponseDTO dto = new ExchangeConversionResponseDTO();
        dto.setSuccess(false);
        doReturn(dto).when(spyService).convertCurrency(anyString(), anyString(), anyDouble());
        String result = spyService.convertAndFormat("USD", "BRL", 100.0);
        assertEquals("Conversion failed.", result);
    }

    @Test
    void testConvertAndFormat_Failure_NullQuery() {
        ExchangeRateService spyService = Mockito.spy(exchangeRateService);
        ExchangeConversionResponseDTO dto = new ExchangeConversionResponseDTO();
        dto.setSuccess(true);
        dto.setQuery(null);
        dto.setInfo(new Info());
        doReturn(dto).when(spyService).convertCurrency(anyString(), anyString(), anyDouble());
        String result = spyService.convertAndFormat("USD", "BRL", 100.0);
        assertEquals("Conversion failed.", result);
    }

    @Test
    void testConvertAndFormat_Failure_NullInfo() {
        ExchangeRateService spyService = Mockito.spy(exchangeRateService);
        ExchangeConversionResponseDTO dto = new ExchangeConversionResponseDTO();
        dto.setSuccess(true);
        Query query = new Query();
        query.setAmount(100.0);
        query.setFrom("USD");
        query.setTo("BRL");
        dto.setQuery(query);
        dto.setInfo(null);
        doReturn(dto).when(spyService).convertCurrency(anyString(), anyString(), anyDouble());
        String result = spyService.convertAndFormat("USD", "BRL", 100.0);
        assertEquals("Conversion failed.", result);
    }

}
