package com.fera.metalurgica.service;

import com.fera.metalurgica.model.Usuario;
import com.fera.metalurgica.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class UsuarioDetailsServiceTest {

	@Autowired
	private UsuarioDetailsService usuarioDetailsService;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Test
	void gerenteRecebeRoleGerente() {
		Usuario gerente = new Usuario(
			null,
			"Maria Gerente",
			"Gerente",
			LocalDate.of(1992, 8, 15),
			"gerente.teste@fera.com",
			passwordEncoder.encode("1234")
		);
		usuarioRepository.save(gerente);

		Set<String> authorities = usuarioDetailsService.loadUserByUsername(gerente.getEmail())
			.getAuthorities()
			.stream()
			.map(GrantedAuthority::getAuthority)
			.collect(Collectors.toSet());

		assertTrue(authorities.contains("ROLE_GERENTE"));
		assertEquals(gerente.getEmail(), usuarioDetailsService.loadUserByUsername(gerente.getEmail()).getUsername());
	}

	@Test
	void administradorRecebeRoleAdmin() {
		Usuario admin = new Usuario(
			null,
			"Lucas Stibbe",
			"Administrador",
			LocalDate.of(2007, 1, 19),
			"admin.teste@fera.com",
			passwordEncoder.encode("1234")
		);
		usuarioRepository.save(admin);

		Set<String> authorities = usuarioDetailsService.loadUserByUsername(admin.getEmail())
			.getAuthorities()
			.stream()
			.map(GrantedAuthority::getAuthority)
			.collect(Collectors.toSet());

		assertTrue(authorities.contains("ROLE_ADMIN"));
		assertTrue(authorities.contains("ROLE_GERENTE"));
	}

	@Test
	void adminPadraoEhCorrigidoNoBoot() {
		Usuario admin = usuarioRepository.findByEmailIgnoreCase("admin@fera.com")
			.orElseThrow();

		assertEquals("Lucas Stibbe", admin.getNome());
		assertEquals("Administrador", admin.getCargo());
		assertTrue(passwordEncoder.matches("1234", admin.getSenha()));
	}
}
