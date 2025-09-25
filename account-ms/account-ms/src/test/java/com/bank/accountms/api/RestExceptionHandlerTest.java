package com.bank.accountms.api;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class RestExceptionHandlerTest {

  private final RestExceptionHandler handler = new RestExceptionHandler();

  @Test
  void testNotFound() {
    ResponseEntity<?> response = handler.notFound(new NoSuchElementException("not found"));
    assertEquals(404, response.getStatusCodeValue());
    assertEquals("not found", response.getBody());
  }

  @Test
  void testBusiness() {
    ResponseEntity<?> response = handler.business(new IllegalStateException("illegal"));
    assertEquals(400, response.getStatusCodeValue());
    assertEquals("illegal", response.getBody());
  }

  @Test
  void testBadRequest() {
    ResponseEntity<?> response = handler.badRequest(new IllegalArgumentException("bad arg"));
    assertEquals(400, response.getStatusCodeValue());
    assertEquals("bad arg", response.getBody());
  }

  @Test
  void testValidation() {
    // Simular un error de validación en el campo "accountNumber"
    BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "account");
    bindingResult.addError(new FieldError("account", "accountNumber", "must not be blank"));

    MethodArgumentNotValidException ex =
        new MethodArgumentNotValidException(null, bindingResult);

    ResponseEntity<?> response = handler.validation(ex);

    assertEquals(400, response.getStatusCodeValue());
    assertTrue(response.getBody() instanceof List);

    @SuppressWarnings("unchecked")
    List<String> errors = (List<String>) response.getBody();
    assertTrue(errors.get(0).contains("accountNumber must not be blank"));
  }
}
