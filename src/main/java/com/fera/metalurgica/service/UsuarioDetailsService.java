package com.fera.metalurgica.service;

import com.fera.metalurgica.model.Usuario;
import com.fera.metalurgica.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class UsuarioDetailsService implements UserDetailsService {

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Usuario usuario = usuarioRepository.findByEmailIgnoreCase(email)
			.orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));

		return User.builder()
			.username(usuario.getEmail())
			.password(usuario.getSenha())
			.roles(determinarRoles(usuario.getCargo()))
			.build();
	}

	private String[] determinarRoles(String cargo) {
		if (cargo == null) {
			return new String[] {"USER"};
		}

		return switch (cargo.trim().toUpperCase(Locale.ROOT)) {
			case "ADMINISTRADOR" -> new String[] {"ADMIN", "GERENTE"};
			case "GERENTE" -> new String[] {"GERENTE"};
			default -> new String[] {"USER"};
		};
	}
}
