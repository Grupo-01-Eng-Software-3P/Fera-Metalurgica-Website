package com.fera.metalurgica.repository;
import com.fera.metalurgica.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
}
