package com.fera.metalurgica.repository;

import com.fera.metalurgica.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
	Categoria findByNome(String nome);
}
