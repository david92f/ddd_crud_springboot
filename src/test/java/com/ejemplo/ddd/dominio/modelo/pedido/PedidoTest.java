package com.ejemplo.ddd.dominio.modelo.pedido;

import com.ejemplo.ddd.dominio.modelo.producto.IdentificadorProducto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PedidoTest {

    private static final Currency EUR = Currency.getInstance("EUR");
    private Pedido pedido;
    private Direccion direccion;
    private IdentificadorProducto productoId;
    private Dinero precioUnitario;

    @BeforeEach
    void setUp() {
        direccion = new Direccion("Calle Falsa 123", "Springfield", "12345", "España");
        pedido = Pedido.crearNuevoPedido("cliente-123", direccion, EUR);
        productoId = IdentificadorProducto.nuevo();
        precioUnitario = new Dinero(new BigDecimal("10.50"), EUR);
    }

    @Test
    @DisplayName("Should create new pedido with correct initial state")
    void shouldCreateNewPedidoWithCorrectInitialState() {
        assertNotNull(pedido.getId());
        assertEquals("cliente-123", pedido.getIdCliente());
        assertEquals(direccion, pedido.getDireccionEnvio());
        assertEquals(EstadoPedido.PENDIENTE, pedido.getEstado());
        assertTrue(pedido.getLineasPedido().isEmpty());
        assertEquals(BigDecimal.ZERO, pedido.getTotalPedido().cantidad());
        assertEquals(EUR, pedido.getTotalPedido().moneda());
        assertNotNull(pedido.getFechaCreacion());
        assertNotNull(pedido.getFechaUltimaModificacion());
    }

    @Test
    @DisplayName("Should throw exception when creating pedido with null idCliente using factory method")
    void shouldThrowExceptionWhenCreatingPedidoWithNullIdCliente() {
        NullPointerException exception = assertThrows(
            NullPointerException.class,
            () -> Pedido.crearNuevoPedido(null, direccion, EUR)
        );
        
        assertTrue(exception.getMessage().contains("El ID del cliente no puede ser nulo"));
    }

    @Test
    @DisplayName("Should throw exception when creating pedido with empty idCliente")
    void shouldThrowExceptionWhenCreatingPedidoWithEmptyIdCliente() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> Pedido.crearNuevoPedido("", direccion, EUR)
        );
        
        assertTrue(exception.getMessage().contains("El ID del cliente no puede estar vacío"));
    }

    @Test
    @DisplayName("Should throw exception when creating pedido with null direccion")
    void shouldThrowExceptionWhenCreatingPedidoWithNullDireccion() {
        NullPointerException exception = assertThrows(
            NullPointerException.class,
            () -> Pedido.crearNuevoPedido("cliente-123", null, EUR)
        );
        
        assertTrue(exception.getMessage().contains("La dirección de envío no puede ser nula"));
    }

    @Test
    @DisplayName("Should throw exception when creating pedido with null currency")
    void shouldThrowExceptionWhenCreatingPedidoWithNullCurrency() {
        NullPointerException exception = assertThrows(
            NullPointerException.class,
            () -> Pedido.crearNuevoPedido("cliente-123", direccion, null)
        );
        
        assertTrue(exception.getMessage().contains("La moneda por defecto no puede ser nula"));
    }

    @Test
    @DisplayName("Should add line to pedido successfully")
    void shouldAddLineToPedidoSuccessfully() {
        pedido.agregarLineaPedido(productoId, 2, precioUnitario);
        
        assertEquals(1, pedido.getLineasPedido().size());
        LineaPedido linea = pedido.getLineasPedido().get(0);
        assertEquals(productoId, linea.getIdProducto());
        assertEquals(2, linea.getCantidad());
        assertEquals(precioUnitario, linea.getPrecioUnitario());
        assertEquals(new BigDecimal("21.00"), pedido.getTotalPedido().cantidad());
    }

    @Test
    @DisplayName("Should throw exception when adding line with null productoId")
    void shouldThrowExceptionWhenAddingLineWithNullProductoId() {
        NullPointerException exception = assertThrows(
            NullPointerException.class,
            () -> pedido.agregarLineaPedido(null, 2, precioUnitario)
        );
        
        assertTrue(exception.getMessage().contains("El ID del producto no puede ser nulo"));
    }

    @Test
    @DisplayName("Should throw exception when adding line with null precioUnitario")
    void shouldThrowExceptionWhenAddingLineWithNullPrecioUnitario() {
        NullPointerException exception = assertThrows(
            NullPointerException.class,
            () -> pedido.agregarLineaPedido(productoId, 2, null)
        );
        
        assertTrue(exception.getMessage().contains("El precio unitario no puede ser nulo"));
    }

    @Test
    @DisplayName("Should throw exception when adding line with different currency")
    void shouldThrowExceptionWhenAddingLineWithDifferentCurrency() {
        Dinero precioUSD = new Dinero(new BigDecimal("10.50"), Currency.getInstance("USD"));
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> pedido.agregarLineaPedido(productoId, 2, precioUSD)
        );
        
        assertTrue(exception.getMessage().contains("La moneda de la nueva línea"));
        assertTrue(exception.getMessage().contains("no coincide con la moneda del pedido"));
    }

    @Test
    @DisplayName("Should throw exception when adding line to sent pedido")
    void shouldThrowExceptionWhenAddingLineToSentPedido() {
        pedido.agregarLineaPedido(productoId, 2, precioUnitario);
        pedido.confirmarPedido();
        pedido.marcarComoEnviado();
        
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> pedido.agregarLineaPedido(IdentificadorProducto.nuevo(), 1, precioUnitario)
        );
        
        assertTrue(exception.getMessage().contains("No se pueden agregar líneas a un pedido en estado"));
    }

    @Test
    @DisplayName("Should remove line from pedido successfully")
    void shouldRemoveLineFromPedidoSuccessfully() {
        pedido.agregarLineaPedido(productoId, 2, precioUnitario);
        pedido.eliminarLineaPedido(productoId);
        
        assertTrue(pedido.getLineasPedido().isEmpty());
        assertEquals(BigDecimal.ZERO, pedido.getTotalPedido().cantidad());
    }

    @Test
    @DisplayName("Should throw exception when removing non-existent line")
    void shouldThrowExceptionWhenRemovingNonExistentLine() {
        pedido.agregarLineaPedido(productoId, 2, precioUnitario);
        IdentificadorProducto otroProductoId = IdentificadorProducto.nuevo();
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> pedido.eliminarLineaPedido(otroProductoId)
        );
        
        assertTrue(exception.getMessage().contains("No se encontró la línea de pedido para el producto"));
    }

    @Test
    @DisplayName("Should update line quantity successfully")
    void shouldUpdateLineQuantitySuccessfully() {
        pedido.agregarLineaPedido(productoId, 2, precioUnitario);
        pedido.actualizarCantidadLineaPedido(productoId, 5);
        
        LineaPedido linea = pedido.getLineasPedido().get(0);
        assertEquals(5, linea.getCantidad());
        assertEquals(new BigDecimal("52.50"), pedido.getTotalPedido().cantidad());
    }

    @Test
    @DisplayName("Should throw exception when updating quantity for non-existent line")
    void shouldThrowExceptionWhenUpdatingQuantityForNonExistentLine() {
        pedido.agregarLineaPedido(productoId, 2, precioUnitario);
        IdentificadorProducto otroProductoId = IdentificadorProducto.nuevo();
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> pedido.actualizarCantidadLineaPedido(otroProductoId, 5)
        );
        
        assertTrue(exception.getMessage().contains("No se encontró la línea de pedido para el producto"));
    }

    @Test
    @DisplayName("Should confirm pedido successfully")
    void shouldConfirmPedidoSuccessfully() {
        pedido.agregarLineaPedido(productoId, 2, precioUnitario);
        pedido.confirmarPedido();
        
        assertEquals(EstadoPedido.PROCESANDO, pedido.getEstado());
    }

    @Test
    @DisplayName("Should throw exception when confirming empty pedido")
    void shouldThrowExceptionWhenConfirmingEmptyPedido() {
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> pedido.confirmarPedido()
        );
        
        assertTrue(exception.getMessage().contains("No se puede confirmar un pedido vacío"));
    }

    @Test
    @DisplayName("Should throw exception when confirming already confirmed pedido")
    void shouldThrowExceptionWhenConfirmingAlreadyConfirmedPedido() {
        pedido.agregarLineaPedido(productoId, 2, precioUnitario);
        pedido.confirmarPedido();
        
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> pedido.confirmarPedido()
        );
        
        assertTrue(exception.getMessage().contains("Solo se pueden confirmar pedidos pendientes"));
    }

    @Test
    @DisplayName("Should mark pedido as sent successfully")
    void shouldMarkPedidoAsSentSuccessfully() {
        pedido.agregarLineaPedido(productoId, 2, precioUnitario);
        pedido.confirmarPedido();
        pedido.marcarComoEnviado();
        
        assertEquals(EstadoPedido.ENVIADO, pedido.getEstado());
    }

    @Test
    @DisplayName("Should throw exception when marking pending pedido as sent")
    void shouldThrowExceptionWhenMarkingPendingPedidoAsSent() {
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> pedido.marcarComoEnviado()
        );
        
        assertTrue(exception.getMessage().contains("Solo se pueden marcar como enviados los pedidos en procesamiento"));
    }

    @Test
    @DisplayName("Should mark pedido as delivered successfully")
    void shouldMarkPedidoAsDeliveredSuccessfully() {
        pedido.agregarLineaPedido(productoId, 2, precioUnitario);
        pedido.confirmarPedido();
        pedido.marcarComoEnviado();
        pedido.marcarComoEntregado();
        
        assertEquals(EstadoPedido.ENTREGADO, pedido.getEstado());
    }

    @Test
    @DisplayName("Should cancel pedido successfully")
    void shouldCancelPedidoSuccessfully() {
        pedido.agregarLineaPedido(productoId, 2, precioUnitario);
        pedido.cancelarPedido("Cliente solicitó cancelación");
        
        assertEquals(EstadoPedido.CANCELADO, pedido.getEstado());
    }

    @Test
    @DisplayName("Should throw exception when canceling delivered pedido")
    void shouldThrowExceptionWhenCancelingDeliveredPedido() {
        pedido.agregarLineaPedido(productoId, 2, precioUnitario);
        pedido.confirmarPedido();
        pedido.marcarComoEnviado();
        pedido.marcarComoEntregado();
        
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> pedido.cancelarPedido("Motivo inválido")
        );
        
        assertTrue(exception.getMessage().contains("No se puede cancelar un pedido que ya ha sido entregado"));
    }

    @Test
    @DisplayName("Should update shipping address successfully")
    void shouldUpdateShippingAddressSuccessfully() {
        Direccion nuevaDireccion = new Direccion("Nueva Calle 456", "Otra Ciudad", "67890", "Francia");
        pedido.actualizarDireccionEnvio(nuevaDireccion);
        
        assertEquals(nuevaDireccion, pedido.getDireccionEnvio());
    }

    @Test
    @DisplayName("Should throw exception when updating address for sent pedido")
    void shouldThrowExceptionWhenUpdatingAddressForSentPedido() {
        pedido.agregarLineaPedido(productoId, 2, precioUnitario);
        pedido.confirmarPedido();
        pedido.marcarComoEnviado();
        
        Direccion nuevaDireccion = new Direccion("Nueva Calle 456", "Otra Ciudad", "67890", "Francia");
        
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> pedido.actualizarDireccionEnvio(nuevaDireccion)
        );
        
        assertTrue(exception.getMessage().contains("No se puede cambiar la dirección de un pedido que ya fue enviado"));
    }

    @Test
    @DisplayName("Should handle multiple lines with same product")
    void shouldHandleMultipleLinesWithSameProduct() {
        pedido.agregarLineaPedido(productoId, 2, precioUnitario);
        pedido.agregarLineaPedido(productoId, 3, precioUnitario);
        
        assertEquals(1, pedido.getLineasPedido().size());
        LineaPedido linea = pedido.getLineasPedido().get(0);
        assertEquals(5, linea.getCantidad());
        assertEquals(new BigDecimal("52.50"), pedido.getTotalPedido().cantidad());
    }

    @Test
    @DisplayName("Should recalculate total when removing last line")
    void shouldRecalculateTotalWhenRemovingLastLine() {
        pedido.agregarLineaPedido(productoId, 2, precioUnitario);
        assertEquals(new BigDecimal("21.00"), pedido.getTotalPedido().cantidad());
        
        pedido.eliminarLineaPedido(productoId);
        assertEquals(BigDecimal.ZERO, pedido.getTotalPedido().cantidad());
    }

    @Test
    @DisplayName("Should test equals and hashCode based on id")
    void shouldTestEqualsAndHashCodeBasedOnId() {
        IdentificadorPedido sameId = pedido.getId();
        Pedido pedidoConMismoId = new Pedido(
            sameId, "otro-cliente", direccion, 
            List.of(), pedido.getTotalPedido(), pedido.getEstado(),
            pedido.getFechaCreacion(), pedido.getFechaUltimaModificacion()
        );
        
        assertEquals(pedido, pedidoConMismoId);
        assertEquals(pedido.hashCode(), pedidoConMismoId.hashCode());
        
        Pedido pedidoConDistintoId = Pedido.crearNuevoPedido("cliente-123", direccion, EUR);
        assertNotEquals(pedido, pedidoConDistintoId);
    }
}