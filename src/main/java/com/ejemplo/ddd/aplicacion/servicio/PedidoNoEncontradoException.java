package com.ejemplo.ddd.aplicacion.servicio;

// Excepción personalizada para cuando no se encuentra un pedido
public class PedidoNoEncontradoException extends RuntimeException {
    public PedidoNoEncontradoException(String message) {
        super(message);
    }
}
