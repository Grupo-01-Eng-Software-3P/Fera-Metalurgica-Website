package com.fera.metalurgica.service;

import com.fera.metalurgica.model.Usuario;
import com.fera.metalurgica.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class UsuarioDetailsService implements UserDetailsService {

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Usuario usuario = usuarioRepository.findByEmail(email)
			.orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));

		return User.builder()
			.username(usuario.getEmail())
			.password(usuario.getSenha())
			.roles("USER")
			.build();
	}
}
