package com.ejemplo.ddd.dominio.modelo.pedido;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class IdentificadorPedidoTest {

    @Test
    @DisplayName("Should create a new identifier successfully")
    void shouldCreateNewIdentifierSuccessfully() {
        IdentificadorPedido identificador = IdentificadorPedido.nuevo();
        
        assertNotNull(identificador);
        assertNotNull(identificador.valor());
        assertFalse(identificador.valor().toString().isEmpty());
    }

    @Test
    @DisplayName("Should create identifier from valid UUID string")
    void shouldCreateIdentifierFromValidUUIDString() {
        String uuidString = "123e4567-e89b-12d3-a456-426614174000";
        IdentificadorPedido identificador = IdentificadorPedido.deString(uuidString);
        
        assertNotNull(identificador);
        assertEquals(UUID.fromString(uuidString), identificador.valor());
    }

    @Test
    @DisplayName("Should throw exception when creating from invalid UUID string")
    void shouldThrowExceptionWhenCreatingFromInvalidUUIDString() {
        String invalidUUID = "invalid-uuid";
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> IdentificadorPedido.deString(invalidUUID)
        );
        
        assertTrue(exception.getMessage().contains("ID de pedido proporcionado no es un UUID válido"));
        assertTrue(exception.getCause() instanceof IllegalArgumentException);
    }

    @Test
    @DisplayName("Should throw exception when creating from null UUID")
    void shouldThrowExceptionWhenCreatingFromNullUUID() {
        NullPointerException exception = assertThrows(
            NullPointerException.class,
            () -> new IdentificadorPedido(null)
        );
        
        assertTrue(exception.getMessage().contains("El valor del identificador de pedido no puede ser nulo"));
    }

    @Test
    @DisplayName("Should create identifier with valid UUID")
    void shouldCreateIdentifierWithValidUUID() {
        UUID validUUID = UUID.randomUUID();
        IdentificadorPedido identificador = new IdentificadorPedido(validUUID);
        
        assertNotNull(identificador);
        assertEquals(validUUID, identificador.valor());
    }

    @Test
    @DisplayName("Should generate different identifiers for multiple calls")
    void shouldGenerateDifferentIdentifiersForMultipleCalls() {
        IdentificadorPedido id1 = IdentificadorPedido.nuevo();
        IdentificadorPedido id2 = IdentificadorPedido.nuevo();
        
        assertNotEquals(id1, id2);
        assertNotEquals(id1.valor(), id2.valor());
    }
}