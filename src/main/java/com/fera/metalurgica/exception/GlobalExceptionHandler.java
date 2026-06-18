package com.fera.metalurgica.exception;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

	// STATUS 404
	@ExceptionHandler(ResourceNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public String handleNotFound(ResourceNotFoundException ex, Model model) {
		model.addAttribute("mensagem", ex.getMessage());
		return "error/404";
	}

	// STATUS 404 - Endpoint não existente
	@ExceptionHandler(NoResourceFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public String handleEndpointNotFound(NoResourceFoundException ex, Model model) {
		model.addAttribute("mensagem", "A página que você procura não existe.");
		return "error/404";
	}

	// STATUS 400
	@ExceptionHandler(BusinessException.class)
	public String handleBusiness(BusinessException ex, RedirectAttributes redirectAttributes) {
		redirectAttributes.addFlashAttribute("erro", ex.getMessage());
		return "redirect:/dashboard";
	}

	// STATUS 500 (FALLBACK)
	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public String handleGeneric(Exception ex, Model model) {
		model.addAttribute("mensagem", "Erro inesperado. Tente novamente mais tarde.");
		return "error/404";
	}
}
