package com.ejemplo.ddd.dominio.modelo.pedido;

import java.util.Objects;

/**
 * Objeto de Valor para la dirección de envío.
 * Es inmutable.
 */
public record Direccion(String calle, String ciudad, String codigoPostal, String pais) {
    public Direccion {
        Objects.requireNonNull(calle, "La calle no puede ser nula");
        if (calle.isBlank()) throw new IllegalArgumentException("La calle no puede estar vacía");
        Objects.requireNonNull(ciudad, "La ciudad no puede ser nula");
        if (ciudad.isBlank()) throw new IllegalArgumentException("La ciudad no puede estar vacía");
        Objects.requireNonNull(codigoPostal, "El código postal no puede ser nulo");
        if (codigoPostal.isBlank()) throw new IllegalArgumentException("El código postal no puede estar vacío");
        Objects.requireNonNull(pais, "El país no puede ser nulo");
        if (pais.isBlank()) throw new IllegalArgumentException("El país no puede estar vacío");
    }
}
