package com.ejemplo.ddd.aplicacion.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO para agregar una nueva línea a un pedido existente.
 */
public record AgregarLineaRequest(
    @NotNull(message = "El ID del producto es obligatorio") UUID idProducto,
    @Positive(message = "La cantidad debe ser positiva") int cantidad,
    @NotNull(message = "El precio unitario es obligatorio") 
    @Positive(message = "El precio unitario debe ser positivo")
    BigDecimal precioUnitario
    // La moneda se tomará del pedido existente.
) {}
