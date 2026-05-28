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
			.csrf(csrf -> csrf
				.ignoringRequestMatchers(
					"/agenda/**",
					"/nova-atividade",
					"/novo-usuario",
					"/pedido",
					"/nova-categoria",
					"/novo-produto",
					"/nova-imagem",
					"/orcamentos/salvar" // Necessário para o formulário de orçamento
				)
			)
			.authorizeHttpRequests(auth -> auth
				.requestMatchers(
					"/login", "/css/**", "/js/**", "/imagens/**", "/uploads/**",
					"/", "/orcamento", "/catalogo", "/pedido"
				).permitAll()
				.requestMatchers(
					"/dashboard",
					"/midia", "/midia/**",
					"/orcamentos", "/orcamentos/**",
					"/usuarios", "/novo-usuario",
					"/agenda", "/agenda/**"
				).authenticated()
				.anyRequest().permitAll()
			)
			.formLogin(form -> form
				.loginPage("/login")
				.usernameParameter("email")
				.passwordParameter("password")
				.defaultSuccessUrl("/dashboard", false)
				.permitAll()
			)
			.logout(logout -> logout
				.logoutSuccessUrl("/login?logout")
			);

		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
