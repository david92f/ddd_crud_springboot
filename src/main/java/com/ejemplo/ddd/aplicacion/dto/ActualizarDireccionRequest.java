package com.ejemplo.ddd.aplicacion.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para la solicitud de actualización de la dirección de envío de un Pedido.
 */
public record ActualizarDireccionRequest(
    @NotNull(message = "La nueva dirección es obligatoria")
    @Valid
    DireccionData nuevaDireccion
) {
    public record DireccionData(
        @NotBlank(message = "La calle es obligatoria") String calle,
        @NotBlank(message = "La ciudad es obligatoria") String ciudad,
        @NotBlank(message = "El código postal es obligatorio") String codigoPostal,
        @NotBlank(message = "El país es obligatorio") String pais
    ) {}
}
