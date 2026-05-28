package com.fera.metalurgica.exception;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ErrorResponse {
	private int status;
	private String error;
	private String message;
	private LocalDateTime timestamp;
	private List<String> details;

}
