package com.fera.metalurgica.repository;

import com.fera.metalurgica.model.Midia;
import com.fera.metalurgica.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MidiaRepository extends JpaRepository<Midia, Long> {
	List<Midia> findByCategoria(Categoria categoria);
	List<Midia> findByCategoriaId(Long categoriaId);
	List<Midia> findByFavoritaTrueOrderByDataUploadDesc();
	Optional<Midia> findByCategoriaIdAndPrincipalTrue(Long categoriaId);

	@Modifying
	@Query("UPDATE Midia m SET m.principal = false WHERE m.categoria.id = :categoriaId")
	void desmarcarPrincipalDaCategoria(@Param("categoriaId") Long categoriaId);
}
