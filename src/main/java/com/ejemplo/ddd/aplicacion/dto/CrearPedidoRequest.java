package com.ejemplo.ddd.aplicacion.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * DTO para la solicitud de creación de un nuevo Pedido.
 * Incluye validaciones de Jakarta Bean Validation.
 */
public record CrearPedidoRequest(
    @NotBlank(message = "El ID del cliente es obligatorio")
    String idCliente,

    @NotNull(message = "La dirección de envío es obligatoria")
    @Valid
    DireccionData direccionEnvio,

    @NotEmpty(message = "El pedido debe tener al menos una línea")
    @Valid
    List<LineaPedidoData> lineas,
    
    @NotBlank(message = "La moneda es obligatoria")
    @Size(min = 3, max = 3, message = "El código de moneda debe tener 3 caracteres")
    String moneda // Ej. "EUR", "USD"
) {
    public record DireccionData(
        @NotBlank(message = "La calle es obligatoria") String calle,
        @NotBlank(message = "La ciudad es obligatoria") String ciudad,
        @NotBlank(message = "El código postal es obligatorio") String codigoPostal,
        @NotBlank(message = "El país es obligatorio") String pais
    ) {}

    public record LineaPedidoData(
        @NotNull(message = "El ID del producto es obligatorio") UUID idProducto,
        @Positive(message = "La cantidad debe ser positiva") int cantidad,
        @NotNull(message = "El precio unitario es obligatorio") 
        @Positive(message = "El precio unitario debe ser positivo")
        BigDecimal precioUnitario
        // La moneda de la línea se asume que es la misma que la del pedido
    ) {}
}
