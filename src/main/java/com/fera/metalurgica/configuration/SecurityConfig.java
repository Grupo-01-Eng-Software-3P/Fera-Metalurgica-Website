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
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
							"/h2-console/**",
								"/login",
								"/login/**",
                                "/css/**",
                                "/js/**",
                                "/imagens/**",
                                "/",
                                "/orcamento",
                                "/catalogo",
								"/catalogo/**",
                                "/pedido"
                        ).permitAll()
                        .requestMatchers(
                                "/dashboard",
								"/agenda",
								"/agenda/**",
                                "/midia",
                                "/orcamentos",
                                "/orcamentos/**",
                                "/usuarios",
								"/novo-usuario",
								"/nova-imagem",
								"/nova-categoria"
                                ).authenticated()
                        		.anyRequest().permitAll()
                )

				.csrf(csrf -> csrf
				.ignoringRequestMatchers("/h2-console/**")
				)

				.headers(headers -> headers
					.frameOptions(frame -> frame.disable())
				)

			.formLogin(form -> form
                        .loginPage("/login")
						.loginProcessingUrl("/login-process")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/dashboard", false)
                        .permitAll()
                )

                .logout(logout -> logout
						.logoutUrl("/logout")
						.logoutSuccessUrl("/login?logout")
						.permitAll()
				);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
