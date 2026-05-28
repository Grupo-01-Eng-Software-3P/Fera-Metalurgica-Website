package com.fera.metalurgica.repository;

import com.fera.metalurgica.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
}
