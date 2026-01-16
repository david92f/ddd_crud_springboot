package com.ejemplo.ddd.aplicacion.dto;

import jakarta.validation.constraints.Positive;

/**
 * DTO para la solicitud de actualización de la cantidad de una línea de Pedido.
 */
public record ActualizarCantidadLineaRequest(
    @Positive(message = "La nueva cantidad debe ser positiva")
    int nuevaCantidad
) {}
