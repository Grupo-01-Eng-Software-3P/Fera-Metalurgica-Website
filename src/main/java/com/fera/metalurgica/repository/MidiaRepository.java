package com.fera.metalurgica.repository;

import com.fera.metalurgica.model.Midia;
import com.fera.metalurgica.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MidiaRepository extends JpaRepository<Midia, Long> {
	List<Midia> findByCategoria(Categoria categoria);
	List<Midia> findByCategoriaId(Long categoriaId);
}
