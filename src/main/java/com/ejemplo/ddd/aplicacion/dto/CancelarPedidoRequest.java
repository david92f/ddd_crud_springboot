package com.ejemplo.ddd.aplicacion.dto;

import jakarta.validation.constraints.NotBlank;

public record CancelarPedidoRequest(
    @NotBlank(message = "El motivo de cancelación es obligatorio")
    String motivo
) {}
