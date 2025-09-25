package com.bank.customerms.api;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class RestExceptionHandlerTest {

  private final RestExceptionHandler handler = new RestExceptionHandler();

  @Test
  void testNotFound() {
    ResponseEntity<?> response = handler.notFound(new NoSuchElementException("Not here"));
    assertEquals(404, response.getStatusCode().value());
  }

  @Test
  void testConflict() {
    ResponseEntity<?> response = handler.conflict(new IllegalStateException("Conflict"));
    assertEquals(409, response.getStatusCode().value());
  }

  @Test
  void testBadRequest() {
    ResponseEntity<?> response = handler.badRequest(new IllegalArgumentException("Bad arg"));
    assertEquals(400, response.getStatusCode().value());
  }

  @Test
  void testValidation() {
    BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "test");
    bindingResult.addError(new FieldError("test", "field1", "must not be blank"));

    MethodArgumentNotValidException ex =
        new MethodArgumentNotValidException(null, bindingResult);

    ResponseEntity<?> response = handler.validation(ex);
    assertEquals(400, response.getStatusCode().value());
    assertTrue(((java.util.List<?>) response.getBody()).get(0).toString().contains("field1"));
  }
}
