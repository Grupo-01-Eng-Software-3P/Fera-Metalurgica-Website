package com.fera.metalurgica.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

	@Value("${upload.dir}")
	private String uploadDir;

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		String caminho = Paths.get(uploadDir).toAbsolutePath().toString();
		registry.addResourceHandler("/imagens/**")
			.addResourceLocations("file:" + caminho + "/");
	}
}
