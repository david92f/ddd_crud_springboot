package com.ejemplo.ddd.dominio.repositorio;

import com.ejemplo.ddd.dominio.modelo.pedido.IdentificadorPedido;
import com.ejemplo.ddd.dominio.modelo.pedido.Pedido;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz del Repositorio para el Agregado Pedido.
 * Define las operaciones de persistencia necesarias desde la perspectiva del dominio.
 */
public interface PedidoRepository {
    void guardar(Pedido pedido);
    Optional<Pedido> buscarPorId(IdentificadorPedido id);
    List<Pedido> buscarTodos();
    void eliminarPorId(IdentificadorPedido id);
    // Podrían existir otros métodos de búsqueda específicos, ej:
    // List<Pedido> buscarPorIdCliente(String idCliente);
    // List<Pedido> buscarPorEstado(EstadoPedido estado);
}
