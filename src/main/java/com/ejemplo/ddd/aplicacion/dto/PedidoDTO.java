package com.ejemplo.ddd.aplicacion.dto;

import com.ejemplo.ddd.dominio.modelo.pedido.EstadoPedido;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO para representar la información de un Pedido en respuestas de la API.
 */
public record PedidoDTO(
    UUID idPedido,
    String idCliente,
    DireccionDTO direccionEnvio,
    List<LineaPedidoDTO> lineasPedido,
    BigDecimal totalPedido,
    String moneda,
    EstadoPedido estado,
    LocalDateTime fechaCreacion,
    LocalDateTime fechaUltimaModificacion
) {
    public record DireccionDTO(String calle, String ciudad, String codigoPostal, String pais) {}

    public record LineaPedidoDTO(
        UUID idProducto,
        int cantidad,
        BigDecimal precioUnitario,
        String moneda,
        BigDecimal subtotal
    ) {}
}
