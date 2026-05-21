package com.fera.metalurgica.repository;

import com.fera.metalurgica.model.Atividade;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AtividadeRepository extends JpaRepository<Atividade, Long> {
    List<Atividade> findAllByOrderByIdDesc();
}