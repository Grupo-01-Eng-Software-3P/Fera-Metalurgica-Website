package com.fera.metalurgica.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

	@Value("${upload.dir:uploads/}")
	private String uploadDir;

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		// Serve os arquivos da pasta de uploads como recursos estáticos em /uploads/**
		String caminho = Paths.get(uploadDir).toAbsolutePath().normalize().toUri().toString();

		registry.addResourceHandler("/uploads/**")
			.addResourceLocations(caminho);
	}
}
