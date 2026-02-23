package com.ejemplo.ddd.dominio.modelo.pedido;

import java.util.Objects;

/**
 * Objeto de Valor para la dirección de envío.
 * Es inmutable.
 */
public record Direccion(String calle, String ciudad, String codigoPostal, String pais) {
    public Direccion {
        validarCampo(calle, "calle");
        validarCampo(ciudad, "ciudad");
        validarCampo(codigoPostal, "código postal");
        validarCampo(pais, "país");
    }

    private static void validarCampo(String valor, String nombreCampo) {
        Objects.requireNonNull(valor, "El campo " + nombreCampo + " no puede ser nulo");
        if (valor.isBlank()) {
            throw new IllegalArgumentException("El campo " + nombreCampo + " no puede estar vacío");
        }
    }
}
