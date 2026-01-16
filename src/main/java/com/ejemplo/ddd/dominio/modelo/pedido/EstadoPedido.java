package com.ejemplo.ddd.dominio.modelo.pedido;

/**
 * Enumeración que representa los posibles estados de un Pedido.
 */
public enum EstadoPedido {
    PENDIENTE,      // El pedido ha sido creado pero no procesado
    PROCESANDO,     // El pedido está siendo preparado
    ENVIADO,        // El pedido ha sido enviado
    ENTREGADO,      // El pedido ha sido entregado al cliente
    CANCELADO       // El pedido ha sido cancelado
}
