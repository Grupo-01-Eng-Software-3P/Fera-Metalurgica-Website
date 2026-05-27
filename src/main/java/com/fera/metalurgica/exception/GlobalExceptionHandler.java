package com.fera.metalurgica.exception;

import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

	// STATUS 404
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
		ErrorResponse error = ErrorResponse.builder()
			.status(HttpStatus.NOT_FOUND.value())
			.error("Not Found")
			.message(ex.getMessage())
			.timestamp(LocalDateTime.now())
			.build();

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
	}

	// STATUS 400 - NEGÓCIO
	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<ErrorResponse> handleBusiness(BusinessException ex) {
		ErrorResponse error = ErrorResponse.builder()
			.status(HttpStatus.BAD_REQUEST.value())
			.error("Bad Request")
			.message(ex.getMessage())
			.timestamp(LocalDateTime.now())
			.build();

		return ResponseEntity.badRequest().body(error);
	}

	// STATUS 400 - VALIDAÇÃO
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
		List<String> details = ex.getBindingResult()
			.getFieldErrors()
			.stream()
			.map(field -> field.getField() + ": " + field.getDefaultMessage())
			.toList();

		ErrorResponse error = ErrorResponse.builder()
			.status(HttpStatus.BAD_REQUEST.value())
			.error("Validation Failed")
			.message("Campos inválidos")
			.timestamp(LocalDateTime.now())
			.details(details)
			.build();

		return ResponseEntity.badRequest().body(error);
	}

	// STATUS 500 (FALLBACK)
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
		ErrorResponse error = ErrorResponse.builder()
			.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
			.error("Internal Server Error")
			.message("Erro inesperado. Tente novamente mais tarde.")
			.timestamp(LocalDateTime.now())
			.build();

		return ResponseEntity.internalServerError().body(error);
	}
}
