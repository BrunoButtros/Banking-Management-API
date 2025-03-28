package dev.bruno.banking.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void testHandleValidationExceptions() {
        Object target = new Object();
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(target, "target");
        FieldError fieldError = new FieldError("target", "field", "must not be null");
        bindingResult.addError(fieldError);
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<Map<String, Object>> response = handler.handleValidationExceptions(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(HttpStatus.BAD_REQUEST.value(), body.get("status"));
        assertEquals("must not be null", body.get("field"));
        assertNotNull(body.get("timestamp"));
    }

    @Test
    void testHandleBusinessException() {
        BusinessException businessEx = new BusinessException("Test business error");
        ResponseEntity<Map<String, Object>> response = handler.handleBusinessException(businessEx);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(HttpStatus.BAD_REQUEST.value(), body.get("status"));
        assertEquals("Business Error", body.get("error"));
        assertEquals("Test business error", body.get("message"));
        assertNotNull(body.get("timestamp"));
    }

    @Test
    void testHandleAllExceptions() {
        Exception ex = new Exception("General error");
        ResponseEntity<Map<String, Object>> response = handler.handleAllExceptions(ex);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), body.get("status"));
        assertEquals("Internal Server Error", body.get("error"));
        assertTrue(((String) body.get("message")).contains("General error"));
        assertNotNull(body.get("timestamp"));
    }
}
