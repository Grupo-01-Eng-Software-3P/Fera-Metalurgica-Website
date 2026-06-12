package com.fera.metalurgica.repository;

import com.fera.metalurgica.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
	Optional<Categoria> findByNomeIgnoreCase(String nome);
}
