package com.fera.metalurgica.repository;

import com.fera.metalurgica.model.CategoriaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<CategoriaEntity, Long> {
	CategoriaEntity findByNome(String nome);
}
