package com.fera.metalurgica.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.csrf(csrf -> csrf.ignoringRequestMatchers(
				"/orcamentos/salvar", "/pedido", "/novo-usuario", "/nova-imagem", "/nova-categoria",
				"/agenda", "/agenda/dados" // Adicionado para permitir o fetch do JS
			))
			.authorizeHttpRequests(auth -> auth
				.requestMatchers("/login", "/css/**", "/js/**", "/imagens/**", "/").permitAll()
				.anyRequest().authenticated()
			)
			.formLogin(form -> form
				.loginPage("/login")
				.usernameParameter("email")
				.passwordParameter("password")
				.defaultSuccessUrl("/dashboard", true)
				.permitAll()
			)
			.logout(logout -> logout.logoutSuccessUrl("/login?logout"));

		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
