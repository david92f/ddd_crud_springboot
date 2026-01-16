package com.ejemplo.ddd.dominio.modelo.producto;

import java.util.UUID;
import java.util.Objects;

/**
 * Objeto de Valor para el identificador único de un Producto.
 * Es inmutable.
 */
public record IdentificadorProducto(UUID valor) {
    public IdentificadorProducto {
        Objects.requireNonNull(valor, "El valor del identificador de producto no puede ser nulo");
    }

    public static IdentificadorProducto nuevo() {
        return new IdentificadorProducto(UUID.randomUUID());
    }

     public static IdentificadorProducto deString(String uuidString) {
        try {
            return new IdentificadorProducto(UUID.fromString(uuidString));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("El ID de producto proporcionado no es un UUID válido: " + uuidString, e);
        }
    }
}
