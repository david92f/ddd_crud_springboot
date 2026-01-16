package com.ejemplo.ddd.dominio.modelo.pedido;

import java.util.UUID;
import java.util.Objects;

/**
 * Objeto de Valor para el identificador único de un Pedido.
 * Es inmutable.
 */
public record IdentificadorPedido(UUID valor) {
    public IdentificadorPedido {
        Objects.requireNonNull(valor, "El valor del identificador de pedido no puede ser nulo");
    }

    public static IdentificadorPedido nuevo() {
        return new IdentificadorPedido(UUID.randomUUID());
    }

    public static IdentificadorPedido deString(String uuidString) {
        try {
            return new IdentificadorPedido(UUID.fromString(uuidString));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("El ID de pedido proporcionado no es un UUID válido: " + uuidString, e);
        }
    }
}
