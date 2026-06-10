package com.fera.metalurgica.dto;

import com.fera.metalurgica.model.Pedido;
import java.util.List;

public record OrcamentosDTO(
	List<Pedido> meusPedidos,
	List<Pedido> clientesComOrcamento,
	List<Pedido> clientesPendentes
) {}
